#include <stdio.h>
#include <gtk/gtk.h>
#include <stdlib.h>

static void print_hello(GtkWidget *widget, char const *data) {
    g_print("Hello : %s\n", data);
}

//=============== 使用 gtk 的 xml 布局完成 UI 构建 ===============
/*
 * gtk 的 xml 布局文件后缀名为 .ui，默认 Clion 打不开，可以在 File Type 配置 XML 类型的文件关联这个后缀。【不用学习如何手动编写 xml 布局，后面会使用 glade】
 */
int main(int argc, char **argv) {
    GError *error = NULL;

    //1：初始化 gtk
    gtk_init(&argc, &argv);

    //2：创建构建器
    GtkBuilder *builder = gtk_builder_new();

    //3：加载布局文件
    if (gtk_builder_add_from_file(builder, "builder2.ui", &error) == 0) {
        g_printerr("Error loading file: %s\n", error->message);
        g_clear_error(&error);
        return 1;
    }

    //4：获取 window
    GObject *window = gtk_builder_get_object(builder, "window");

    //5：绑定一些事件
    g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);

    GObject *button1 = gtk_builder_get_object(builder, "button1");
    g_signal_connect(button1, "clicked", G_CALLBACK(print_hello), "button1");

    GObject *button2 = gtk_builder_get_object(builder, "button2");
    g_signal_connect(button2, "clicked", G_CALLBACK(print_hello), "button2");

    GObject *quit = gtk_builder_get_object(builder, "quit");
    g_signal_connect(quit, "clicked", G_CALLBACK(gtk_main_quit), NULL);

    //6：解绑构建器，方便框架释放
    g_object_unref(builder);

    //gtk_builder_get_application(builder);//获取 application

    //7：启动
    gtk_main();

    return 0;
}
