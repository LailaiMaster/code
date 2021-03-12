#include <stdio.h>

//=============== 判断是否在 C 或者 C++ 中运行===============
#ifdef __cplusplus
extern "C" {
#endif

int Add(int left, int right);

#ifdef __cplusplus
};
#endif
//=============== 判断是否在 C 或者 C++ 中运行===============

//=============== 常用宏定义指令===============
/*
 * 1. #ifdef 如果定义了
 * 2. #ifndef 如果没定义
 * 3. #if 如果 ...
 *
 * #endif
 *
 *  #if defined(MACRO) ==> #ifdef MACRO
 */

void dump(char *message) {
#ifdef DEBUG
    puts(message);
#endif
}

//=============== 使用宏实现条件编译 ===============
int main() {
    dump("main start");
    printf("Hello world!\n");
    dump("main end.");

    //=============== 查看 C 版本===============
#if __STDC_VERSION__ >= 201112
    puts("C11!!");
#elif __STDC_VERSION__ >= 199901
    puts("C99!!");
#else
    puts("maybe C90?");
#endif
    return 0;
}
