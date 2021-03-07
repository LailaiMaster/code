#include "fibonacci.h"

unsigned int Fibonacci(unsigned int n) {
  if (n == 1 || n == 0) {
    return 1;
  } else {
    return Fibonacci(n - 1) + Fibonacci(n - 2);
  }
}