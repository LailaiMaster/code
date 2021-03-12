#include <stdio.h>
#include "io_utils.h"

#define ARRAY_SIZE 5

//=============== 数组的边界 ===============
int main() {
    //=============== 明确定义数组的范围 ===============
    int array[ARRAY_SIZE];
    int array_with_expression[3 + 2];
    PRINT_INT(array[0]);
    PRINT_INT(array[5]);//数组越界不报错，值未知。

    //=============== 变长数组（VLA） ===============
    // C99 开始支持，gcc 在 C99 前单独做了支持。
    // MSVC 不支持。
    int value = 2;
    int array_size_of_value[value];

    const int kSize = 5; // C++ 支持，C++ 中常量是真的常量。
    int array_size_of_const[kSize];
    return 0;
}
