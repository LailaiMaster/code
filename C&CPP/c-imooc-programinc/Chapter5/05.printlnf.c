#include <stdio.h>
#include <stdarg.h>

//版本1：使用函数
void Printlnf(const char *format, ...) {
    va_list args;
    va_start(args, format);
    //v 开头的版本就是用来接收 va_list 参数的。
    vprintf(format, args);
    printf("\n");
    va_end(args);
}

/**
 * 版本2：使用宏函数
 *  1. __VA_ARGS__ 用于在宏函数获取边长参数。
 *  2. 在处理可变参数时，##__VA_ARGS__ 中的的两个 #，用于在调用者没有传递可变参数时，去掉前面的 ","。【另外 ## 可用来链接它两边的标识符成为一个新的标识符】
 *  3. __FILE__ 用于替换为文件路径。
 *  4. __LINE__ 表示行号。
 *  5. __FUNCTION__ 表示函数名。
 *  6. 在宏替换中一个 "#"  + 宏函数参数，用于被替换为参数名字面量，如果调用 PRINT_INT(x)，则 #value 被替换为 x。
 */
#define PRINTLNF_S(format, ...) printf(format"\n",  ##__VA_ARGS__)
#define PRINTLNF(format, ...) printf("("__FILE__":%d) %s : "format"\n",__LINE__, __FUNCTION__, ##__VA_ARGS__)
#define PRINT_INT(value) PRINTLNF(#value": %d", value)

//=============== 实现一个带换行符的 printf ===============
int main() {

    int value = 2;
    Printlnf("Hello World! %d", value);
    PRINTLNF_S("Hello World! %d", value);
    PRINTLNF("Hello World! %d", value);
    PRINTLNF("Hello World!");

    PRINT_INT(value); // value: 2

    int x = 3;
    PRINT_INT(x);
    PRINT_INT(3 + 4);

    return 0;
}