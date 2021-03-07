
#include <gtk/gtk.h>
#include "utils/ui_utils.h"
#include "ui/ui_new_task.h"
#include "ui/ui_task_list.h"

static void SetupButtons(GtkBuilder *builder) {
  GtkButton *add_button = (GtkButton *) gtk_builder_get_object(builder, "add_button");
  g_signal_connect(add_button, "clicked", G_CALLBACK(OpenNewTaskDialog), NULL);

  GtkButton *remove_button = (GtkButton *) gtk_builder_get_object(builder, "remove_button");
  g_signal_connect_swapped(remove_button, "clicked", G_CALLBACK(RemoveSelectedTask), NULL);

  GtkButton *resume_button = (GtkButton *) gtk_builder_get_object(builder, "resume_button");
  g_signal_connect_swapped(resume_button, "clicked", G_CALLBACK(ResumeSelectedTask), NULL);

  GtkButton *pause_button = (GtkButton *) gtk_builder_get_object(builder, "pause_button");
  g_signal_connect_swapped(pause_button, "clicked", G_CALLBACK(PauseSelectedTask), NULL);
}

static void GtkMain(int argc, char **argv) {
  gtk_init(&argc, &argv);

  GtkBuilder *builder = OpenWindowWithStyle("downloader.ui", "downloader.css", 0);

  if (!builder) {
    g_abort();
  }

  SetupButtons(builder);

  InitTaskList(builder);
  gtk_main();
}

int RunDownloaderMain(int argc, char **argv) {
  GtkMain(argc, argv);
  return 0;
}
