#include <iostream>

#include "factorial.h"
#include "fibonacci.h"

//=============== 从 CPP 调用 C ===============
/*
 * CPP 编译期编译时会对函数进行重命名，如果项目中的 c 文件不加特殊处理，则无法找到 c 中的函数。【
 *      1. 可以用 objdump -t xxx.obj 查看文件内的符号。
 *      2. 可以用 c++filt.exe 命令还原符号。
 *
 * 解决方案：告诉 g++ 的哪些函数是 C 函数。extern "C" { ... }
 */
int main() {
    std::cout << Factorial(5) << std::endl;
    std::cout << Fibonacci(5) << std::endl;
    return 0;
}