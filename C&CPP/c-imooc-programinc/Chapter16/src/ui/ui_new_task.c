//
// Created by benny on 9/27/20.
//

#include "ui/ui_new_task.h"
#include <stdlib.h>
#include <glib.h>
#include "model/task_info.h"
#include "ui/ui_task_list.h"

#include "utils/io_utils.h"
#include "utils/file_utils.h"
#include "model/request_api.h"

#define URL_PATTERN "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

static struct NewTaskDialogContext {
  GtkBuilder *builder;
  GtkDialog *dialog;
  GtkEntry *url_entry;
  GtkLabel *url_error_label;
  GtkFileChooserButton *directory_choose_button;
  GtkLabel *directory_error_label;
  GtkSpinner *ok_spinner;
  GtkButton *ok_button;
  GtkButton *cancel_button;
} *context;

static gboolean OnTaskCreatedSuccessfully(TaskInfo *task_info) {
  if (context) {
    AddTaskToList(task_info);

    free(task_info);
    gtk_dialog_response(context->dialog, 1);
  }
  return FALSE;
}

static gboolean OnTaskCreatedFailed(char const *message) {
  if(context) {
    gtk_spinner_stop(context->ok_spinner);

    gtk_widget_set_sensitive(GTK_WIDGET(context->ok_button), TRUE);
    gtk_widget_set_sensitive(GTK_WIDGET(context->cancel_button), TRUE);

    gtk_window_set_deletable(GTK_WINDOW(context->dialog), TRUE);

    ShowAlertDialog(GTK_WINDOW(context->dialog), message);
  }
  return FALSE;
}

static void OnGetTaskInfoSuccessfully(void *receiver, void *data) {
  TaskInfo *task_info = (TaskInfo *) data;
  int result = InsertTaskInfo(task_info);
  if(result == RESULT_OK) {
    gdk_threads_add_idle(G_SOURCE_FUNC(OnTaskCreatedSuccessfully), task_info);
  } else {
    gdk_threads_add_idle(G_SOURCE_FUNC(OnTaskCreatedFailed), "Failed to insert into database.");
  }
}

static void OnGetTaskInfoFailed(void *receiver, int code, char const *message) {
  gdk_threads_add_idle(G_SOURCE_FUNC(OnTaskCreatedFailed), (gpointer) message);
}

static void CreateTask(char const *url, char const *directory) {
  GetTaskInfo(url, directory, OnGetTaskInfoSuccessfully, OnGetTaskInfoFailed);
}

static void OnConfirmCreateTask(GtkWidget *widget, gpointer data) {
  if(!context) {
    return;
  }

  char const *input_url = gtk_entry_get_text(context->url_entry);
  PRINTLNF("url: %s", input_url);

  int error_found = 0;
  if (g_regex_match_simple(URL_PATTERN, input_url, 0, 0) != 0) {
    gtk_widget_set_visible(GTK_WIDGET(context->url_error_label), 0);
  } else {
    gtk_widget_set_visible(GTK_WIDGET(context->url_error_label), 1);
    error_found = 1;
  }

  char const *chosen_directory = gtk_file_chooser_get_filename(GTK_FILE_CHOOSER(context->directory_choose_button));
  PRINTLNF("chosen_directory: %s", chosen_directory);
  if (chosen_directory) {
    gtk_widget_set_visible(GTK_WIDGET(context->directory_error_label), 0);
  } else {
    PRINTLNF("Please choose a directory to save.");
    gtk_widget_set_visible(GTK_WIDGET(context->directory_error_label), 1);

    error_found = 1;
  }

  if (!error_found) {
    gtk_spinner_start(context->ok_spinner);

    gtk_widget_set_sensitive(GTK_WIDGET(context->ok_button), FALSE);
    gtk_widget_set_sensitive(GTK_WIDGET(context->cancel_button), FALSE);

    gtk_window_set_deletable(GTK_WINDOW(context->dialog), FALSE);

    CreateTask(input_url, chosen_directory);
  }
}

static void OnDialogDestroyed(GtkWidget *widget, gpointer data) {
  free(context);
  context = NULL;
  PRINTLNF("Destroy new task dialog...");
}

void OpenNewTaskDialog(GtkWidget *widget, gpointer data) {
  GtkBuilder *builder = OpenWindowWithStyle("downloader_new_task.ui", "downloader.css", 0);

  if (!builder) {
    g_abort();
  }

  context = malloc(sizeof(struct NewTaskDialogContext));
  context->builder = builder;
  context->dialog = (GtkDialog *) gtk_builder_get_object(builder, "window");
  g_signal_connect(context->dialog, "destroy", G_CALLBACK(OnDialogDestroyed), NULL);

  context->ok_button = (GtkButton *) gtk_builder_get_object(builder, "ok_button");
  g_signal_connect_swapped(context->ok_button, "clicked", G_CALLBACK(OnConfirmCreateTask), NULL);

  context->cancel_button = (GtkButton *) gtk_builder_get_object(builder, "cancel_button");

  context->url_entry = (GtkEntry *) gtk_builder_get_object(builder, "url_entry");
  context->url_error_label = (GtkLabel *) gtk_builder_get_object(builder, "url_error_label");

  char *copied_text = gtk_clipboard_wait_for_text(gtk_clipboard_get_default(gdk_display_get_default()));
  if(copied_text) {
    gtk_entry_set_text(context->url_entry, copied_text);
    PRINTLNF("Copied: %s", copied_text);
  } else {
    PRINTLNF("No copied text.");
  }

  context->directory_choose_button = (GtkFileChooserButton *) gtk_builder_get_object(builder, "directory_choose_button");
  context->directory_error_label = (GtkLabel *) gtk_builder_get_object(builder, "directory_error_label");

  char const *home_dir = g_get_home_dir();
  char default_directory[strlen(home_dir) + 10];
  JoinPath(default_directory, home_dir, "Downloads");

  gtk_file_chooser_set_filename(GTK_FILE_CHOOSER(context->directory_choose_button), default_directory);

  context->ok_spinner = (GtkSpinner *) gtk_builder_get_object(builder, "ok_spinner");

  gint response = gtk_dialog_run(context->dialog);

  PRINTLNF("response: %d", response);

  gtk_widget_destroy(GTK_WIDGET(context->dialog));
}
