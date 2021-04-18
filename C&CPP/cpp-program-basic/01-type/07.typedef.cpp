/*
 ============================================================================
 
 Author      : Ztiany
 Description : typedef 命令

 ============================================================================
 */
#include <cstdio>
#include <cstdlib>
#include <iostream>

//使用 typedef 定义类型别名
typedef int Integer;
typedef const char *String;

int main() {

    Integer i = 3;
    std::cout << "int i = " << i << std::endl;

    return EXIT_SUCCESS;
}
