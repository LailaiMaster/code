#include <stdio.h>
#include <gtk/gtk.h>
#include <stdlib.h>

/**
 * 添加 G_MODULE_EXPORT 用于支持 GTKBuilder 为 xml 中配置的信号绑定处理函数。
 */
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

//=============== 使用创建 glade 创建  gtk 的 xml 布局，完成 UI 构建 ===============
int main(int argc, char **argv) {
    GError *error = NULL;

    gtk_init(&argc, &argv);

    GtkBuilder *builder = gtk_builder_new();
    if (gtk_builder_add_from_file(builder, "builder2.ui", &error) == 0) {
        g_printerr("Error loading file: %s\n", error->message);
        g_clear_error(&error);
        return 1;
    }

    //手动绑定信号和处理函数
    GObject *window = gtk_builder_get_object(builder, "window");
    g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);

    GObject *button1 = gtk_builder_get_object(builder, "button1");
    g_signal_connect(button1, "clicked", G_CALLBACK(print_hello), "button1");

    //GObject *button2 = gtk_builder_get_object(builder, "button2");
    //g_signal_connect(button2, "clicked", G_CALLBACK(print_hello), "button2");

    GObject *quit = gtk_builder_get_object(builder, "quit");
    g_signal_connect(quit, "clicked", G_CALLBACK(gtk_main_quit), NULL);

    //通过 gtk builder 自动解析 xml 中的信号，然后绑定处理函数。【这个函数在 Windows 上需要 gmodule-export 模块的支持】
    //具体参考：
    //  https://developer.gnome.org/gtk3/stable/GtkBuilder.html#gtk-builder-connect-signals
    //  https://stackoverflow.com/questions/9331974/handling-events-from-a-gtkwidget-built-from-an-xml-file
    // 还有一个 gtk_builder_connect_signals_full 函数功能类似，但是不会自动绑定信号的处理函数，而是传入一个 GtkBuilderConnectFunc 来指定信号处理函数。这个函数不需要  gmodule-export 模块的支持。
    gtk_builder_connect_signals(builder, NULL);

    //执行
    gtk_main();

    //最后才释放资源，因为上面要用到。
    g_object_unref(builder);

    return 0;
}
