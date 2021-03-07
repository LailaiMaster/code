#include "utils/file_utils.h"
#include "utils/io_utils.h"
#include <wchar.h>

#ifdef __WIN32
#include <stringapiset.h>
#  define DIR_SEPARATOR '\\'
#else
#  define DIR_SEPARATOR '/'
#endif

void JoinPath(char *destination, const char *path1, const char *path2) {
  if (path1 && *path1) {
    size_t len = strlen(path1);
    strcpy(destination, path1);

    if (destination[len - 1] == DIR_SEPARATOR) {
      if (path2 && *path2) {
        strcpy(destination + len, (*path2 == DIR_SEPARATOR) ? (path2 + 1) : path2);
      }
    }
    else {
      if (path2 && *path2) {
        if (*path2 == DIR_SEPARATOR)
          strcpy(destination + len, path2);
        else {
          destination[len] = DIR_SEPARATOR;
          strcpy(destination + len + 1, path2);
        }
      }
    }
  }
  else if (path2 && *path2)
    strcpy(destination, path2);
  else
    destination[0] = '\0';
}

FILE *SmartFOpen(const char *filename, const char *mode) {
  FILE *file;

#ifdef _WIN32
  /*
   * MinGW 不支持设置 locale，因此传入的文件名如果为 UTF8 编码的CJK 字符，则通过 fopen 打开时被默认为 GBK 编码
   * 通过以下方法将 utf-8 编码的窄字符转为宽字符，通过 wfopen 打开文件即可保证编码的正确性
   */
  wchar_t w_output_filename[MAX_PATH];
  MultiByteToWideChar(CP_UTF8, 0, filename, -1, w_output_filename, 1024);

  int mode_length = strlen(mode) * sizeof(wchar_t) + 1;
  wchar_t w_mode[mode_length];
  MultiByteToWideChar(CP_UTF8, 0, mode, -1, w_mode, mode_length);
  file = _wfopen(w_output_filename, w_mode);
#else
  file = fopen(filename, mode);
#endif
  return file;
}

FILE *SmartFOpenFileInDirectory(const char *directory, const char *filename, const char *mode) {
  char full_filename[1024];
  JoinPath(full_filename, directory, filename);
  return SmartFOpen(full_filename, mode);
}
