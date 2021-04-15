/*
 ============================================================================

 Author      : Ztiany
 Description : 定义类的构造函数与析构函数

 ============================================================================
 */


#include "05.constructor.h"
#include <iostream>

using namespace std;

int main() {
    Line line(10.0);

    // 获取默认设置的长度
    cout << "Length of line : " << line.getLength() << endl;
    // 再次设置长度
    line.setLength(6.0);
    cout << "Length of line : " << line.getLength() << endl;
}