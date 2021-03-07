#include <io_utils.h>
#include <string.h>
#include <stdlib.h>
#include <wchar.h>
#include <wctype.h>
#include <locale.h>

int main() {
  // support for wide string
  {
    PRINT_BOOL(iswalpha(L'A'));
    PRINT_BOOL(iswdigit(L'2'));

    wchar_t *wcs = L"你好 Hello";
    size_t length = wcslen(wcs);
    PRINT_INT(length);

    wchar_t src[] = L"HelloWorld";
    wchar_t *dest = malloc(sizeof(wchar_t) * 11);
    wmemset(dest, 0, 11);
    wmemcpy(dest, src, 11);
    _putws(dest);
    wmemcpy(dest + 3, dest + 1, 4);
    _putws(dest);
    free(dest);
  }

  // conversions
  char *new_locale = setlocale(LC_ALL, "zh_CN.utf8");
  if (new_locale) {
    puts(new_locale);
  }

  {
    char mbs[] = "你好";
    wchar_t wcs[10];
    mbstowcs(wcs, mbs, 10);
    wprintf(L"%s\n", wcs);
  }
  return 0;
}
