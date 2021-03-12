#include "gtk_ui.h"
#include <stdlib.h>
#include <gtk/gtk.h>

#define input_0 "0"
#define input_1 "1"
#define input_2 "2"
#define input_3 "3"
#define input_4 "4"
#define input_5 "5"
#define input_6 "6"
#define input_7 "7"
#define input_8 "8"
#define input_9 "9"
#define input_plus "+"
#define input_minus "-"
#define input_multiply "*"
#define input_divide "/"
#define input_equal "="
#define input_clear "C"
#define input_sign "S"
#define input_percent "%"
#define input_point "."

#define BIND_BUTTON(builder, input_value) \
{GtkButton* button_input_##input_value = (GtkButton *) gtk_builder_get_object(builder, "input_"#input_value); \
g_signal_connect(button_input_##input_value, "clicked", G_CALLBACK(OnInputButtonClicked), input_##input_value);}


static UiContext *ui_context;

static void DisplayTextWithGtkLabel(char *text) {
    gtk_label_set_text(ui_context->text_displayer, text);
}

static void OnInputButtonClicked(void *widget, void *data) {
    HandleInput(ui_context->context, ((char *) data)[0]);
}

static void GtkMain(int argc, char *argv[]) {
    gtk_init(&argc, &argv);

    GError *error = NULL;

    GtkCssProvider *css_provider = gtk_css_provider_new();
    if (gtk_css_provider_load_from_path(css_provider, "calculator.css", &error) == 0) {
        g_printerr("Error loading css file: %s\n", error->message);
        g_clear_error(&error);
        return;
    }

    GtkBuilder *builder = gtk_builder_new();
    if (gtk_builder_add_from_file(builder, "calculator.ui", &error) == 0) {
        g_printerr("Error loading layout file: %s\n", error->message);
        g_clear_error(&error);
        return;
    }

    GtkWindow *window = (GtkWindow *) gtk_builder_get_object(builder, "window");
    gtk_style_context_add_provider_for_screen(
            gtk_window_get_screen(window),
            (GtkStyleProvider *) css_provider,
            GTK_STYLE_PROVIDER_PRIORITY_USER
    );
    g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);
    ui_context->text_displayer = (GtkLabel *) gtk_builder_get_object(builder, "display");

    /*
     * 宏替换后的样子：
     * {
     *      GtkButton * button_input_0 = (GtkButton *) gtk_builder_get_object(builder,  "input_0");
     *      g_signal_connect_data((button_input_0), ("clicked"), (((GCallback)(OnInputButtonClicked))), "0", NULL, 0);
     * }
     */
    BIND_BUTTON(builder, 0)
    BIND_BUTTON(builder, 1)
    BIND_BUTTON(builder, 2)
    BIND_BUTTON(builder, 3)
    BIND_BUTTON(builder, 4)
    BIND_BUTTON(builder, 5)
    BIND_BUTTON(builder, 6)
    BIND_BUTTON(builder, 7)
    BIND_BUTTON(builder, 8)
    BIND_BUTTON(builder, 9)
    BIND_BUTTON(builder, plus)
    BIND_BUTTON(builder, minus)
    BIND_BUTTON(builder, multiply)
    BIND_BUTTON(builder, divide)
    BIND_BUTTON(builder, equal)
    BIND_BUTTON(builder, clear)
    BIND_BUTTON(builder, sign)
    BIND_BUTTON(builder, percent)
    BIND_BUTTON(builder, point)

    HandleInput(ui_context->context, '0');

    gtk_main();

    gtk_style_context_remove_provider_for_screen(gtk_window_get_screen(window), (GtkStyleProvider *) css_provider);
    g_object_unref(css_provider);
    g_object_unref(builder);
}

static void InitConsole() {
    ui_context = malloc(sizeof(UiContext));
    ui_context->context = CreateCalcContext();
    ui_context->context->display_text = DisplayTextWithGtkLabel;
}

static void DestroyConsole() {
    DestroyCalcContext(&ui_context->context);
    free(ui_context);
    ui_context = NULL;
}

int RunGtkUi(int argc, char *argv[]) {
    InitConsole();
    GtkMain(argc, argv);
    DestroyConsole();
    return 0;
}