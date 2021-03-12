#include <stdio.h>
#include <io_utils.h>

//=============== 左值与右值 ===============
int main() {
    //【左边是地址，右边是值，左边是箱子，右边是要装在箱子内的东西】
    // left: storage
    // right: value

    int a;
    a = 2;
    int *p = &a;
    *p = 2;
    int b = *p;

    // &a = p; // ERROR!
    // *p + 1 = 3; // ERROR!
    int array[4] = {0};
    int *pa = array;
    *pa = 2;
    *(pa++) = 3;
    *(pa + 2) = 4;
    PRINT_INT_ARRAY(array, 4);
    return 0;
}
