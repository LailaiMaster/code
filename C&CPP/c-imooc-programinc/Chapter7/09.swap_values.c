#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <io_utils.h>

void SwapInt(int *a, int *b) {
  int temp = *a;
  *a = *b;
  *b = temp;
}

void Swap(void *first, void *second, size_t size) {
  void *temp = malloc(size);
  if (temp) {
    memcpy(temp, first, size);
    memcpy(first, second, size);
    memcpy(second, temp, size);

    free(temp);
  } else {
    // ...
  }
}

// msvc C++ decltype(a)
#define SWAP(a, b, type) do { type temp = a; a = b; b = temp; }while (0)
#define SWAP_EXT(a, b) do { typeof(a) temp = a; a = b; b = temp; }while (0)

int main() {
  int a = 0;
  int b = 1;

  double x = 3.0;
  double y = 4.0;

  // SwapInt
  SwapInt(&a, &b);
  Swap(&x, &y, sizeof(double ));

  if(a) SWAP(a, b, int); else {
    puts("no swap.");
  }
  //SWAP_EXT(x, y); // gcc OK; msvc ERROR

  //typeof(a) number = 0; // gcc OK; msvc ERROR
  // int number = 0;

  PRINT_INT(a);
  PRINT_INT(b);
  PRINT_DOUBLE(x);
  PRINT_DOUBLE(y);
  return 0;
}
