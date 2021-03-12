#include <stdio.h>

/*
 * 1. 函数原型。
 * 2. 使用 (void) 明确表示函数没有参数，而 () 表示不确定有多少个参数。
 */
void EmptyParamList(void);

/*
 * 函数调用的条件，调用前要有函数原型声明，原型声明包括以下要素：
 *  1. 函数名。
 *  2. 函数返回值类型，如果没写，默认为 int。
 *  3. 函数参数列表，参数类型，和参数的顺序，而参数形参名不重要。
 */
int Add(int, int);


//=============== 函数原型 ===============
int main(void) {
    puts("");
    EmptyParamList();

    int result = Add(1, 2);
    printf("result of add: %d\n", result);
    return 0;
}

void EmptyParamList(void) {
    puts("Hello");
}