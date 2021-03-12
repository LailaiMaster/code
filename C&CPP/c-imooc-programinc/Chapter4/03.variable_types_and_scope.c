#include <stdio.h>

// file scope：文件作用域
int global_var = 1;

void LocalStaticVar(void) {
    // 静态变量
    // 1. 作用域全局，内存不会因函数退出而销毁
    // 2. int 初值默认为 0
    static int static_var;

    // 自动变量
    // 1. 函数、块作用域，随着函数和块退出而销毁
    // 2. 没有默认初值
    int non_static_var;

    printf("static var: %d\n", static_var++);
    printf("non static var: %d\n", non_static_var++);
}

double Add(double a, double b);

void CleanMemory() {
    int eraser = -1;
}

// proto scope：原型作用域，只在函数原型上可访问。
double Sort(int size, int array[size]);

void PassByMemory(int parameter) {
    printf("%d\n", parameter);
}

/**寄存器变量一般不需要程序员自己声明，编译期自己会进行优化。*/
void PassByRegister(register int parameter) {
    printf("%d\n", parameter);
}

//=============== 变量类型与作用域 ===============
int main(void) {
    // function scope：函数作用域

    // 自动变量
    auto int value = 0;

    { // block scope：块作用域
        auto int a = 0;
        printf("%d\n", a);
    }

    if (value > 0) {
        int is_value_equals_0 = 0, b = is_value_equals_0;
    } else {

    }

    LocalStaticVar();
    CleanMemory();
    LocalStaticVar();
    CleanMemory();
    LocalStaticVar();
    return 0;
}