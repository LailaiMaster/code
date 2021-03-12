#include <stdio.h>
#include <gtk/gtk.h>
#include <stdlib.h>

static void print_hello(GtkWidget *widget, gpointer data) {
    g_print("Hello World\n");
}

static void activate(GtkApplication *app, gpointer user_data) {
    //g_object_set(gtk_settings_get_default(), "gtk-theme-name", "Windows10", NULL);
    g_object_set(gtk_settings_get_default(), "gtk-theme-name", "Desert-1.2", NULL);
    GtkWidget *window = gtk_application_window_new(app);
    gtk_window_set_title(GTK_WINDOW(window), "HelloWorld");
    gtk_window_set_default_size(GTK_WINDOW(window), 200, 200);

    GtkButtonBox *button_box = gtk_button_box_new(GTK_ORIENTATION_HORIZONTAL);
    gtk_container_add(GTK_CONTAINER(window), button_box);
    GtkButton *button = gtk_button_new_with_label("Hello World");
    gtk_container_add(GTK_CONTAINER(button_box), button);

    g_signal_connect(button, "clicked", G_CALLBACK(print_hello), NULL);
    g_signal_connect_swapped(button, "clicked", G_CALLBACK(gtk_window_close), window);

    gtk_widget_show_all(window);
}

//=============== 为 gtk 应用配置 windows 主题 ===============
/*
 * 资源网站：http://gnome-look.org/
 *
 * 如何应用主题：
 *
 *      方式1，全局修改：
 *              1. 修改 gtk 的配置文件，在 msys64\mingw64\etc\gtk-3.0\ 目录下，打开或创建 settings.init 文件。输入内容如下：
 *                          [Settings]
 *                              gtk-theme-name=Windows10
 *                              gtk-font-name=Segoe UI 9
 *              2. 将主题资源文件放在：msys64\mingw64\share\themes 目录下。
 *
 *      方式2，通过参数局部应用：
 *          1. 将主题资源放在程序运行对应的目录下：
 *                      bin
 *                          04.config_themes.exe
 *                      share/themes
 *                          Windows10【w10 的主题】
 *                          Desert-1.2【mac 的主题】
 *          2. 通过启动参数或者代码指定 gtk 主题，比如：
 *                  2.1：在 PowerShell 中启动程序并指定主题：
 *                              2.2.1. 配置环境变量：$env:GTK_THEME='Windows10'
 *                              2.2.2. 执行程序：.\04.config_themes.exe【如果不是通过 Clion 运行程序会报 lib 找不到，因为程序依赖的 dll 都在 msys64\mingw64\bin 目录下，所以需要将这个目录加入到 PATH 下】
 *                  2.2：在代码中设置：
 *                              g_object_set(gtk_settings_get_default(), "gtk-theme-name", "Windows10", NULL);
 *                              程序会在当前运行目录的 ../share/themes 目录和 msys64\mingw64\share\themes 目录下寻找是否有指定的主题。
 *
 *      方式3，为当前程序添加配置文件：
 *                      bin
 *                          04.config_themes.exe
 *                          libgtk-3.0.dll【同时还需要将这个 lib 复制到同级目录下，否则应用的就是 msys64\mingw64\bin  下的 libgtk-3.0.dll，加载的也是那个目对应的配置文件。】
 *                      etc/gtk-3.0
 *                          settings.ini
 *                      share/themes
 *                          Windows10【w10 的主题】
 *                          Desert-1.2【mac 的主题】
 *
 * Windows 窗口优化，在配置主题的时候，如果窗口顶部上的按钮还是 Windows 风格的，显得格格不入，这时可以添加环境变量 ：
 *                  $env:ETK_CSD=1【powershell】
 *        让也设置窗口顶部上的按钮也是用主题样式。
 *         或者通过代码：
 *              putenv("GTK_CSD=1");
 *         也可以设置。
 */
int main(int argc, char **argv) {
    //设置环境变量
    putenv("GTK_CSD=1");

    //运行程序
    GtkApplication *app = gtk_application_new("com.bennyhuo.clang", G_APPLICATION_FLAGS_NONE);
    g_signal_connect(app, "activate", G_CALLBACK(activate), NULL);
    int status = g_application_run(app, argc, argv);
    g_object_unref(app);

    return status;
}
