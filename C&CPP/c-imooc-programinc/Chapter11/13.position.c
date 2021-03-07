#include <stdio.h>
#include <io_utils.h>
#include <locale.h>

int main() {
  FILE *file = fopen("CMakeLists.txt", "rb");
  long position = ftell(file);
  PRINT_LONG(position);
  char buffer[128];
  fread(buffer, 1, 128, file);
  PRINT_LONG(ftell(file));
  fseek(file, 10, SEEK_SET);
  PRINT_LONG(ftell(file));
  fseek(file, 10, SEEK_CUR);
  PRINT_LONG(ftell(file));
  fseek(file, -10, SEEK_END);
  PRINT_LONG(ftell(file));
  fseek(file, 0, SEEK_END);
  PRINT_LONG(ftell(file));

  fclose(file);

  PRINT_LONG(ftell(stdout));
  perror("ftell(stdout)");

  return 0;
}
