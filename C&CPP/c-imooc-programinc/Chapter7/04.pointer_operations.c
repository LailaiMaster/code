#include <stdio.h>
#include <io_utils.h>

//=============== 指针的运算 ===============
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
        PRINT_INT(sizeof(double));
    }

    {
        double a = 2.0;
        double *p = &a;
        double **pp = &p;

        PRINT_INT(pp + 1);
        PRINT_INT(pp);
        PRINT_INT(sizeof(double *));
    }

    //=============== array 就是个指针常量 ===============
    {
        int array[] = {0, 1, 2, 3, 4};
        int *p = array;
        PRINT_INT(*(p + 3));
        PRINT_INT(*(array + 3));
        PRINT_INT(*(3 + array));
        PRINT_INT(3[array]); // 可以，但是 not recommend
        PRINT_INT(array[3]);
        PRINT_INT(p[3]);

        int *const array_p = array;

        //=============== 指针的比较，比较的是地址，仅限于连续的内存 ===============
        PRINT_BOOL(p + 3 > p + 1);
        PRINT_BOOL(p + 3 == p + 3);

        int array2[] = {3, 4};
        int *p2 = array2;
        PRINT_BOOL(p + 3 == p2 + 1); // meaningless，无意义
        PRINT_BOOL(p + 3 > p2 + 1); // meaningless，无意义
    }

    return 0;
}
