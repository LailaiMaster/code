#include <stdio.h>
#include <stdlib.h>
#include <io_utils.h>

// 1
int *(f1(int, double));

// 2
int (*f2)(int, double);

// 3
int *(*f3)(int, double);

// 4
// int (*f4)(int, double)[];

// 5
// int (*f5)[](int, double);

typedef int (*Func)(int, double);

typedef int Boolean;
typedef int *IntPtr;
typedef int IntArray[];

int Add(int left, double right) {
  return (int) (left + right);
}

void InitPointer(int **ptr, int length, int default_value) {
  *ptr = malloc(sizeof(int) * length);
  for (int i = 0; i < length; ++i) {
    (*ptr)[i] = default_value;
  }
}

int main() {
  int a;
  IntPtr p;
  IntArray players = {1,3,4,5};
  PRINT_HEX(&main);
  PRINT_HEX(&InitPointer);

  void (*func)(int **ptr, int length, int default_value) = &InitPointer;

  func(&p, 10, 0);
  InitPointer(&p, 10, 0);

  (*func)(&p, 10, 0);
  (*InitPointer)(&p, 10, 0);


  PRINT_INT_ARRAY(p, 10);

  free(p);

  Func func1 = &Add;
  int result = func1(1, 3.0);
  PRINT_INT(result);
  return 0;
}
