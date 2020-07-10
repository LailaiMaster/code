/*
 ============================================================================
 
 Author      : Ztiany
 Description : 可移植的数据类型

 ============================================================================
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
//inttypes.h 引入于C99标准，提供了各种位宽的整数类型输入输出时的转换标志宏
#include <inttypes.h>
//stdint.h 引入于C99的标准，主要用于统一跨平台数据定义
#include <stdint.h>
//wchar.h 引入于C95的标准，扩展多字节和宽字符工具
#include <wchar.h>
//wctype.h 引入于C95的标准，用来确定包含于宽字符数据中的类型的函数
#include <wctype.h>

void wIntegerType();
void wCharType();

int main() {
    printf("wIntegerType ------------------------------------------------------------------------------------\n");
    wIntegerType();
    printf("wCharType ---------------------------------------------------------------------------------------\n");
    wCharType();
    return EXIT_SUCCESS;
}

void wIntegerType() {//精确宽度整数类型（exact-width integer type）
    uint8_t uint8_a = 3;
    uint8_t uint8_b = 3;
    uint16_t uint16_a = 3;
    int32_t int32_a = 44;

    //最小宽度类型（minimum width type）
    int_least8_t intLeast8_a = 2;

    //最快最小宽度类型（fastst minimum width type）
    int_fast8_t fast8 = 32;

    //最大整数类型
    intmax_t intmax = 32;

    printf("sizeof(uint8_t) = %zd\n", sizeof(uint8_a));
    printf("sizeof(uint16_t) = %zd\n", sizeof(uint16_a));
    printf("sizeof(int_least8_t) = %zd\n", sizeof(intLeast8_a));
    printf("sizeof(int_fast8_t) = %zd\n", sizeof(fast8));
    printf("sizeof(intmax_t) = %zd\n", sizeof(intmax));

    //sizeof 返回类型
    size_t size = sizeof(32);
    printf("sizeof(32) = %zd\n", size);
    //C还定义了ptrdiff_t类型和t修饰符来表示系统使用的两个地址差值的底层有符号整数类型
    ptrdiff_t ptrdiff = &uint8_a - &uint8_b;

    uintmax_t i = INTMAX_MAX;
    wprintf(L"The largest integer value is %020" PRIxMAX "\n", i);
}

/**
 * 参考：http://docs.linuxtone.org/ebooks/C&CPP/c/apas03.html
 */
void wCharType() {
    //L 告诉编译器该字符串按宽字符保存－即每个字符占用2个字节。
    wchar_t wc_zhong = L'中';
    wchar_t *ws_zhong = L"中";
    char *zhong_code = "\u4e2d";
    printf("len(L'中') = %llu\n", wcslen(ws_zhong));
    printf("sizeof(L'中') = %zd\n", sizeof(wc_zhong));
    printf("sizeof(L'\u4e2d') = %zd\n", strlen(zhong_code));
}