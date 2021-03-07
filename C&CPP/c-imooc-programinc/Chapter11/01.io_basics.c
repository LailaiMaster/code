#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

int main() {
  FILE *file = fopen("CMakeLists.txt", "r");
  if (file) {
    //...
    puts("Open successfully.");
    int err = ferror(file);
    PRINT_INT(err);
    int eof = feof(file);
    PRINT_INT(eof);
    fclose(file);
  } else {
    PRINT_INT(errno);
    puts(strerror(errno));
    perror("fopen");
  }

//  for (int i = 0; i < 10; ++i) {
//    PRINT_INT(i);
//    puts(strerror(i));
//  }

  stdout;
  stderr;
  stdin;
  return 0;
}
