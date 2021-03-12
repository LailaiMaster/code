#include <stdio.h>
#include <gtk/gtk.h>

static void print_hello(GtkWidget *widget, gpointer data) {
    g_print("Hello World\n");
}

/**
 * 被 gtk 回调的函数，参数规定如下：
 * @param app GtkApplication
 * @param user_data 传递过来的数据
 */
static void activate(GtkApplication *app, gpointer user_data) {
    //创建 window
    GtkWidget *window = gtk_application_window_new(app);
    //窗口的 title
    // GTK_WINDOW 用于将 widget 转为 window，有很多类型的 widget，window 是其中一种
    gtk_window_set_title(GTK_WINDOW(window), "HelloWorld");
    //窗口的大小
    gtk_window_set_default_size(GTK_WINDOW(window), 200, 200);

    //创建按钮的容器
    GtkButtonBox *button_box = gtk_button_box_new(GTK_ORIENTATION_HORIZONTAL);
    //往窗口中添加 button_box
    gtk_container_add(GTK_CONTAINER(window), button_box);
    //创建按钮
    GtkButton *button = gtk_button_new_with_label("Hello World");
    //往按钮的容器中添加 button
    gtk_container_add(GTK_CONTAINER(button_box), button);

    //按钮被点击的回调
    g_signal_connect(button, "clicked", G_CALLBACK(print_hello), NULL);
    //点击按钮关闭窗口，g_signal_connect_swapped 相比 g_signal_connect，意思就是回调函数的第一个参数省略掉，只需要 window 这个参数。
    //具体参考 https://stackoverflow.com/questions/2171724/what-does-g-signal-connect-swapped-do
    g_signal_connect_swapped(button, "clicked", G_CALLBACK(gtk_window_close), window);

    //展示窗口
    gtk_widget_show_all(window);
}

//=============== gtk 入门示例 ===============
/*
 * gtk 开头的函数属于 gkt 框架。
 * g 开头的函数属于 glib 框架。
 */
int main(int argc, char **argv) {
    // 1：每个 GUI 程序都有对应的一个 Application
    GtkApplication *app = gtk_application_new("com.bennyhuo.clang", G_APPLICATION_FLAGS_NONE);
    //2：绑定回调函数，设置 gui 程序激活的时候传递什么参数。
    /*
     * activate：信号名
     * G_CALLBACK(activate)：收到信号调用的函数
     * NULL：传给 activate 的参数
     */
    g_signal_connect(app, "activate", G_CALLBACK(activate), NULL);

    //3：运行程序
    int status = g_application_run(app, argc, argv);

    //4：程序结束，解开引用。
    g_object_unref(app);
    return status;
}
