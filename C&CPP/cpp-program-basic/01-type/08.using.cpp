/*
 ============================================================================
 
 Author      : Ztiany
 Description : using 定义类型别名

 ============================================================================
 */

#include <cstdlib>
#include <cstdio>

// C++11 扩展了关键字 using 的用法，增加了 typedef 的能力，可以定义类型别名。它的格式与 typedef 正好相反，别名在左边，原名在右边，是标准的赋值形式，所以易写易读。
using u_int = unsigned int;

int main() {
    u_int int_1 = 32;
    return EXIT_SUCCESS;
}