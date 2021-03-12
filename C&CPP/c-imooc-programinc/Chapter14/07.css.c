#include <stdio.h>
#include <gtk/gtk.h>
#include <stdlib.h>

//=============== 使用 css 调整布局样式 ===============
int main(int argc, char **argv) {
    GError *error = NULL;

    gtk_init(&argc, &argv);

    GtkCssProvider *css_provider = gtk_css_provider_new();
    if (gtk_css_provider_load_from_path(css_provider, "builder3.css", &error) == 0) {
        g_printerr("Error loading css file: %s\n", error->message);
        g_clear_error(&error);
        return 1;
    }

    GtkBuilder *builder = gtk_builder_new();
    if (gtk_builder_add_from_file(builder, "builder3.ui", &error) == 0) {
        g_printerr("Error loading builder file: %s\n", error->message);
        g_clear_error(&error);
        return 1;
    }

    //应用 css
    GObject *window = gtk_builder_get_object(builder, "window");
    gtk_style_context_add_provider_for_screen(
            gtk_window_get_screen(GTK_WINDOW(window)),
            (GtkStyleProvider *) css_provider,
            GTK_STYLE_PROVIDER_PRIORITY_USER);

    g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);

    GObject *quit = gtk_builder_get_object(builder, "quit");
    g_signal_connect(quit, "clicked", G_CALLBACK(gtk_main_quit), NULL);

    gtk_main();
    g_object_unref(builder);

    return 0;
}
