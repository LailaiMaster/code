#include <stdio.h>
#include <gtk/gtk.h>
#include <stdlib.h>

G_MODULE_EXPORT void print_hello2(GtkWidget *widget, GtkTextBuffer *data) {
  GtkTextIter start;
  GtkTextIter end;
  gtk_text_buffer_get_start_iter(data, &start);
  gtk_text_buffer_get_end_iter(data, &end);
  g_print("Hello2 : %s\n", gtk_text_buffer_get_text(data, &start, &end, FALSE));
}

static void print_hello(GtkWidget *widget, char const *data) {
  g_print("Hello : %s\n", data);
}

int main(int argc, char **argv) {
  GError *error = NULL;

  gtk_init(&argc, &argv);

  GtkCssProvider *css_provider = gtk_css_provider_new();

  if(gtk_css_provider_load_from_path(css_provider, "builder3.css", &error) == 0) {
    g_printerr("Error loading css file: %s\n", error->message);
    g_clear_error(&error);
    return 1;
  }

  GtkBuilder *builder = gtk_builder_new();
  if (gtk_builder_add_from_file(builder, "builder3.ui", &error) == 0){
    g_printerr("Error loading builder file: %s\n", error->message);
    g_clear_error(&error);
    return 1;
  }

  GObject *window = gtk_builder_get_object(builder, "window");
  gtk_style_context_add_provider_for_screen(gtk_window_get_screen(GTK_WINDOW(window)),
                                            (GtkStyleProvider *) css_provider, GTK_STYLE_PROVIDER_PRIORITY_USER);

  g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);

  GObject *button1 = gtk_builder_get_object(builder, "button1");
  g_signal_connect(button1, "clicked", G_CALLBACK(print_hello), "button1");
  GObject *button2 = gtk_builder_get_object(builder, "button2");
  //g_signal_connect(button2, "clicked", G_CALLBACK(print_hello), "button2");
  gtk_builder_connect_signals(builder, NULL);

  GObject *quit = gtk_builder_get_object(builder, "quit");
  g_signal_connect(quit, "clicked", G_CALLBACK(gtk_main_quit), NULL);

  gtk_main();

  g_object_unref(builder);

  return 0;
}
