#include <stdio.h>
#include <io_utils.h>
#include <locale.h>
#include <wchar.h>

#define ERROR_ILLEGAL_FILENAME -1
#define ERROR_CANNOT_OPEN_FILE -2
#define ERROR_READ_FILE -3
#define ERROR_UNSUPPORTED_CHARSET -99

#define CHARSET_UTF8 0
#define CHARSET_GBK 1

int CountCharactersInFile(char const *filename, int charset) {
  if (!filename) return ERROR_ILLEGAL_FILENAME;

  FILE *file;

  switch (charset) {
    case CHARSET_GBK:
#ifdef _WIN32
      setlocale(LC_ALL, "chs");
#else
      setlocale(LC_ALL, "zh_CN.gbk");
#endif
      file = fopen(filename, "r");
      break;
    case CHARSET_UTF8:setlocale(LC_ALL, "zh_CN.utf-8");
#ifdef _WIN32
      file = fopen(filename, "r, ccs=utf-8");
#else
      file = fopen(filename, "r");
#endif
      break;
    default:return ERROR_UNSUPPORTED_CHARSET;
  }

  if (!file) return ERROR_CANNOT_OPEN_FILE;

#define BUFFER_SIZE 512
  wchar_t wcs[BUFFER_SIZE];
  int count = 0;
  while (fgetws(wcs, BUFFER_SIZE, file)) {
    count += wcslen(wcs);
  }

  if (ferror(file)) {
    perror("CountCharactersInFile error");
    fclose(file);
    return ERROR_READ_FILE;
  }

  fclose(file);

  return count;
}

int main() {
  PRINT_INT(CountCharactersInFile("data/sanguo_utf8.txt", CHARSET_UTF8));
  PRINT_INT(CountCharactersInFile("data/sanguo_gbk.txt", CHARSET_GBK));
  return 0;
}
