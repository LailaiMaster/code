#ifndef BASICC_DOWNLOADER_UI_UTILS_H_
#define BASICC_DOWNLOADER_UI_UTILS_H_

#include <gtk/gtk.h>

GtkBuilder *OpenWindowWithStyle(char *layout_path, char *style_path, int quit_on_destroy);

gboolean ShowConfirmDialog(GtkWindow *window, char const *message);

void ShowAlertDialog(GtkWindow *window, char const *message);
#endif //BASICC_DOWNLOADER_UI_UTILS_H_
