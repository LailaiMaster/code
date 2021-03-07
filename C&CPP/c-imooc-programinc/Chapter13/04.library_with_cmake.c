#include <stdio.h>

#include "fibonacci/fibonacci.h"
#include "factorial/factorial.h"

int main(void) {

  printf("Fibonacci(5): %d\n", Fibonacci(5));
  printf("Factorial(5): %d\n", Factorial(5));

  return 0;
}