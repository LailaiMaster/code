#include <stdio.h>
#include "io_utils.h"

#define ARRAY_SIZE 5

int main() {
  int array[ARRAY_SIZE];
  int array_with_expression[3 + 2];
  PRINT_INT(array[0]);
  PRINT_INT(array[5]);

  int value = 2;

  // ....
  // C99, VLA; gcc OK; MSVC ERROR
  int array_size_of_value[value];

  const int kSize = 5; // C++ OK
  int array_size_of_const[kSize]; // C99, VLA; gcc OK; MSVC ERROR

  // array[5] => array + 5
  return 0;
}
