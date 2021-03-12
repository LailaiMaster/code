//方式1：引入头文件
#include <stdio.h>

//方式2：引入头文件不是必须的，也可以通过声明函数原型来引入函数，但是引入头文件更加方便，头文件由库开发者提供，我们只管用就行。
//int __cdecl puts(char const*);
//int __cdecl printf(const char * __restrict__ _Format,...);

//=============== 预处理 ===============
int main() {
    puts("Hello, World!");
    printf("Hello World!!!");
    return 0;
}
