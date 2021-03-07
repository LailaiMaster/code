#include <stdio.h>
#include <stdarg.h>

void Printlnf(const char *format, ...) {
  va_list args;
      va_start(args, format);
  vprintf(format, args);
  printf("\n");
      va_end(args);
}

// "Hello ""world" ==> "Hello world"
// __FILE__
// __LINE__
// __FUNCTION__
// (../05.printlnf.c:20) main :

#define PRINTLNF(format, ...) printf("("__FILE__":%d) %s : "format"\n",__LINE__, __FUNCTION__, ##__VA_ARGS__)

#define PRINT_INT(value) PRINTLNF(#value": %d", value)

int main() {

  int value = 2;
  Printlnf("Hello World! %d", value);
  PRINTLNF("Hello World! %d", value);
  PRINTLNF("Hello World!");

  PRINT_INT(value); // value: 2
  int x = 3;
  PRINT_INT(x);

  PRINT_INT(3 + 4);
  return 0;
}
