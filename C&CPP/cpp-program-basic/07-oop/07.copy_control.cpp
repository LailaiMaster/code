/*
 ============================================================================
 
 Author      : Ztiany
 Description : 复制控制

 ============================================================================
 */

#include "07.copy_control.h"
#include <iostream>
#include <cstdlib>

using namespace std;

int main() {

    string serialNo("a");

    //将调用构造函数
    Book book1(12, serialNo);
    //将调用复制构造函数
    Book book2 = book1;
    //将调用拷贝操作符
    book2 = Book(123, serialNo);

    return EXIT_SUCCESS;
}
