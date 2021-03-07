#include <stdio.h>
#include <io_utils.h>

int main() {
  {
    int a = 2;
    int *p = &a;

    PRINT_INT(p + 1);
    PRINT_INT(p);
    PRINT_INT(sizeof(int));
  }

  {
    double a = 2.0;
    double *p = &a;

    PRINT_INT(p + 1);
    PRINT_INT(p);
    PRINT_INT(sizeof(double ));
  }

  {
    double a = 2.0;
    double *p = &a;
    double **pp = &p;

    PRINT_INT(pp + 1);
    PRINT_INT(pp);
    PRINT_INT(sizeof(double *));
  }
  {
    int array[] = {0, 1, 2, 3, 4};
    int *p = array;
    PRINT_INT(*(p + 3));
    PRINT_INT(*(array + 3));
    PRINT_INT(*(3 + array));
    PRINT_INT(3[array]); // not recommend
    PRINT_INT(array[3]);
    PRINT_INT(p[3]);

    int *const array_p = array;

    PRINT_BOOL(p + 3 > p + 1);
    PRINT_BOOL(p + 3 == p + 3);

    int array2[] = {3,4};
    int *p2 = array2;
    PRINT_BOOL(p + 3 == p2 + 1); // meaningless
    PRINT_BOOL(p + 3 > p2 + 1); // meaningless
  }
  return 0;
}
