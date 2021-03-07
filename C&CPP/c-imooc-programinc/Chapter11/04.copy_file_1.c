#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

#define COPY_SUCCESS 0
#define COPY_ILLEGAL_ARGUMENTS -1
#define COPY_SRC_OPEN_ERROR -2
#define COPY_SRC_READ_ERROR -3
#define COPY_DEST_OPEN_ERROR -4
#define COPY_DEST_WRITE_ERROR -5
#define COPY_UNKNOWN_ERROR -100

int CopyFile(char const *src, char const *dest) {
  if (!src || !dest) {
    return COPY_ILLEGAL_ARGUMENTS;
  }

  FILE *src_file = fopen(src, "rb");
  if (!src_file) {
    return COPY_SRC_OPEN_ERROR;
  }

  FILE *dest_file = fopen(dest, "wb");
  if (!dest_file) {
    fclose(src_file);
    return COPY_DEST_OPEN_ERROR;
  }

  int result;

  while (1) {
    int next = fgetc(src_file);
    if (next == EOF) {
      if (ferror(src_file)) {
        result = COPY_SRC_READ_ERROR;
      } else if(feof(src_file)) {
        result = COPY_SUCCESS;
      } else {
        result = COPY_UNKNOWN_ERROR;
      }
      break;
    }

    if (fputc(next, dest_file) == EOF) {
      result = COPY_DEST_WRITE_ERROR;
      break;
    }
  }

  fclose(src_file);
  fclose(dest_file);

  return result;
}

int main() {
  int result = CopyFile("data/io_utils.h", "data_copy/io_utils.h");
  PRINT_INT(result);
  result = CopyFile("data/logo.bmp", "data_copy/logo.bmp");
  PRINT_INT(result);
  return 0;
}
