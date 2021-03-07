#include "utils/io_utils.h"

void PrintBinary(unsigned int value) {
  unsigned int i;
  for (i = 8 * (value > USHRT_MAX ? sizeof(int) : sizeof(short)); i > 0; --i) {
    printf("%d", value >> (i - 1) & 1u);
  }
  printf("\n");
}
