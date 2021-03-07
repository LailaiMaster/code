#include <io_utils.h>
#include <ctype.h>

int IsDigit(char c) {
  return c >= '0' && c <= '9';
}

int main() {
  PRINT_INT(isdigit('0'));
  PRINT_INT(isspace(' '));
  PRINT_INT(isalpha('a'));
  PRINT_INT(isalnum('f'));
  PRINT_INT(isalnum('1'));
  PRINT_INT(ispunct(','));


  return 0;
}
