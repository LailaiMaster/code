#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

void Echo() {
#define BUFFER_SIZE 4
  int buffer[BUFFER_SIZE];
  while (1) {
    size_t bytes_read = fread(buffer, sizeof(buffer[0]), BUFFER_SIZE,stdin);
    PRINT_INT_ARRAY(buffer, 4);
    if (bytes_read < BUFFER_SIZE) {
      if (feof(stdin)) {
        puts("EOF");
        fwrite(buffer, sizeof (char), bytes_read, stdout);
      } else if(ferror(stdin)) {
        perror("Error read from stdin");
      }
      break;
    }
    fwrite(buffer, sizeof(buffer[0]), BUFFER_SIZE, stdout);
  }
}

int main() {
  Echo();
  return 0;
}
