#include <stdio.h>
#include "io_utils.h"

//=============== 二维数组作为参数 ===============
//数组作为参数传递是，编译器只认一维数组，二维数组不能传 int [][]，必须指定长度：int array[][columns]。
// 变长数组：C99; gcc;
// 变长数组：msvc 不支持
void SumIntArrays(int rows, int columns, int array[][columns], int result[]) {
    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < columns; ++j) {
            result[i] += array[i][j];
        }
    }
}

//=============== 二维数组 ===============
int main() {
    int vehicle_limits_good[5][2] = {
            {0, 5},
            {1, 6},
            {2, 7},
            {3, 8},
            {4, 9}
    };

    int vehicle_limits[5][2] = {
            0, 5, 1, [1][1] = 6, 2, 7, 3, 8, 4, 9
    };

    for (int i = 0; i < 5; ++i) {
        for (int j = 0; j < 2; ++j) {
            vehicle_limits[i][j] = i + j;
        }
    }

    int scores[5][4] = {
            {135, 135, 138, 277},
            {105, 134, 108, 265},
            {113, 107, 145, 232},
            {123, 99,  140, 227},
            {98,  118, 127, 242}
    };

    int result[5] = {0};//使用初始话列表，既可以对数组所有元素进行初始化。

    SumIntArrays(5, 4, scores, result);
    PRINT_INT_ARRAY(result, 5);
    return 0;
}
