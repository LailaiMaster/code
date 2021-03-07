#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

void ReadFile() {
  FILE *file = fopen("CMakeLists.txt", "r");
  if (file) {

    int next_char = getc(file);
    while (next_char != EOF) {
      putchar(next_char);
      next_char = getc(file);
    }
    fclose(file);
  } else {
    PRINT_INT(errno);
    puts(strerror(errno));
    perror("fopen");
  }
}

int main() {
//  while (1) {
//    int next_input = getchar();
//    if (next_input == EOF) {
//      break;
//    } else if(next_input == '\n') {
//      continue;
//    }
//    putchar(next_input);
//  }

  ReadFile();

  return 0;
}
