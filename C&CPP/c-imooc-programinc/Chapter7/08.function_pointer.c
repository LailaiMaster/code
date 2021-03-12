#include <stdio.h>
#include <stdlib.h>
#include <io_utils.h>

// 1
int *(f1(int, double));

// 2
int (*f2)(int, double);

// 3
int *(*f3)(int, double);

// 4
// int (*f4)(int, double)[];//错误，不存在函数数组

// 5
// int (*f5)[](int, double);//错误，不存在函数数组，因为每个函数都是独一无二的。【元素为函数】
int (*f5[])(int, double);//可以存在函数指针数组，[] 先和 f5 结合-->函数数组-->再和 * 结合-->函数指针的数组【元素为指针】

//=============== 类型别名 ===============
/*
 * 复杂的函数指针定义，可用 https://cdecl.org/ 网站进行翻译。
 */
typedef int (*Func)(int, double);
typedef int Boolean;
typedef int *IntPtr;
typedef int IntArray[];

int Add(int left, double right) {
    return (int) (left + right);
}

void InitPointer(int **ptr, int length, int default_value) {
    *ptr = malloc(sizeof(int) * length);
    for (int i = 0; i < length; ++i) {
        (*ptr)[i] = default_value;
    }
}

//=============== 函数指针 ===============
int main() {
    int a;
    IntPtr p;
    IntArray players = {1, 3, 4, 5};
    PRINT_HEX(&main);
    PRINT_HEX(&InitPointer);

    void (*func)(int **ptr, int length, int default_value) = &InitPointer;

    func(&p, 10, 0);
    InitPointer(&p, 10, 0);

    (*func)(&p, 10, 0);
    //函数名本质就是函数的地址
    (*InitPointer)(&p, 10, 0);

    PRINT_INT_ARRAY(p, 10);

    free(p);

    Func func1 = &Add;
    int result = func1(1, 3.0);
    PRINT_INT(result);
    return 0;
}
