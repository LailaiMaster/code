#include "utils/ui_utils.h"
#include "utils/io_utils.h"

typedef struct WindowStyleContext {
  GtkBuilder *builder;
  GtkCssProvider *provider;
} WindowStyleContext;

static void OnWindowDestroyed(GtkWindow *window, WindowStyleContext *context) {
  gtk_style_context_remove_provider_for_screen(gtk_window_get_screen(window),
                                               (GtkStyleProvider *) context->provider);

  g_object_unref(context->provider);
  g_object_unref(context->builder);
  g_free(context);
  PRINTLNF("Destroy window: %#x", window);
}

GtkBuilder *OpenWindowWithStyle(char *layout_path, char *style_path, int quit_on_destroy) {
  GError *error = NULL;
  GtkCssProvider *css_provider = gtk_css_provider_new();
  if (gtk_css_provider_load_from_path(css_provider, style_path, &error) == 0) {
    g_printerr("Error loading css file: %s\n", error->message);
    g_clear_error(&error);
    return NULL;
  }

 Construct a GtkBuilder instance and load our UI description

  GtkBuilder *builder = gtk_builder_new();

  if (gtk_builder_add_from_file(builder, layout_path, &error) == 0) {
    g_printerr("Error loading layout file: %s\n", error->message);
    g_clear_error(&error);
    return NULL;
  }

 Connect signal handlers to the constructed widgets.

  GtkWindow *window = (GtkWindow *) gtk_builder_get_object(builder, "window");
  gtk_style_context_add_provider_for_screen(gtk_window_get_screen(window),
                                            (GtkStyleProvider *) css_provider, GTK_STYLE_PROVIDER_PRIORITY_USER);

  WindowStyleContext *context = g_malloc(sizeof(WindowStyleContext));
  context->builder = builder;
  context->provider = css_provider;
  g_signal_connect_after (window, "destroy", G_CALLBACK(OnWindowDestroyed), context);
  if (quit_on_destroy) {
    g_signal_connect (window, "destroy", G_CALLBACK(gtk_main_quit), NULL);
  }

  return builder;
}

gboolean ShowConfirmDialog(GtkWindow *window, char const *message) {
  GtkDialog *alert_dialog = (GtkDialog *) gtk_message_dialog_new(
      GTK_WINDOW(window),
      GTK_DIALOG_MODAL,
      GTK_MESSAGE_QUESTION,
      GTK_BUTTONS_OK_CANCEL,
      "%s", message);

  int response = gtk_dialog_run(alert_dialog);
  printf("response:%d\n", response);

  gtk_widget_destroy(GTK_WIDGET(alert_dialog));

  return response == GTK_RESPONSE_OK;
}

void ShowAlertDialog(GtkWindow *window, const char *message) {
  GtkDialog *alert_dialog = (GtkDialog *) gtk_message_dialog_new(
      GTK_WINDOW(window),
      GTK_DIALOG_MODAL,
      GTK_MESSAGE_ERROR,
      GTK_BUTTONS_CLOSE,
      "%s", message);

  gtk_dialog_run(alert_dialog);

  gtk_widget_destroy(GTK_WIDGET(alert_dialog));
}
