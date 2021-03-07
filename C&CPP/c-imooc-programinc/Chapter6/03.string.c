#include <stdio.h>
#include "io_utils.h"

int main() {
  char string[] = "Hello World";

  for (int i = 0; i < 11; ++i) {
    PRINT_CHAR(string[i]);
  }
  // short %hd
  // NULL \0
  PRINTLNF("%s", string);

  char string_zh[] = "你好，中国";

  wchar_t ws[] = L"你好，中国";
  return 0;
}
