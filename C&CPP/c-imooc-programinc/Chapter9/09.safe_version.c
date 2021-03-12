//虽然 C11 制定了标准，但是并没有要求编译器一定要支持，C11 标准要求在导入 string.h 前先定义下面宏，如果编译器确实支持这些新特新，就会把那些函数放出来。
#define __STDC_WANT_LIB_EXT1__ 1

#include <io_utils.h>
#include <string.h>
#include <stdlib.h>

//=============== C11 引入的安全版本的函数【比如 strcpy_s 比 strcpy 更安全，linux gcc 不支持。】 ===============
int main() {
#ifdef __STDC_LIB_EXT1__//到底不能用那些新功能，通过 __STDC_LIB_EXT1__ 来判断。
    puts("support C11 safe version.");
#else
    puts("No support for C11 safe version.");
#endif

    char dst[2];
    int error_no = strcpy_s(dst, 2, "Too long!");// 2 表示 dst 中有多大的内存可以容纳元素。
    PRINT_INT(error_no); // 34
    if (error_no) {
        perror("strcpy_s returns: "); // strcpy_s returns: : Result too large
    }
    return 0;
}
