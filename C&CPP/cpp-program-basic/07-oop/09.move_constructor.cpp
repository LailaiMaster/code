/*
 ============================================================================
 
 Author      : Ztiany
 Description : 对象移动

 ============================================================================
 */

#include "09.move_constructor.h"
#include <cstdlib>
#include <iostream>
#include <vector>

using namespace std;

void sample1() {
    int i = 42;
    int &r = i;
    //int &&rr = i; 错误
    //int &r2 = i * 24; 错误
    const int &r3 = i * 24;
    int &&rr2 = i * 24;
}

void sample2() {
    int vi = 3;
    int &&rri = std::move(vi);
    cout << "address  rri = " << &rri << endl;
    cout << "address vi = " << &vi << endl;
}

void sample3() {
    //MoveClass moveClass1("a");
    // const MoveClass &moveClass2 = moveClass1;//不会创建一个新的对象，moveClass2 也指向 moveClass1 所指向的对象。
    // MoveClass moveClass3 = moveClass1;
    //MoveClass moveClass4{std::move(MoveClass("b"))};
    MoveClass moveClass5(MoveClass("c"));
}

void sample4() {
    vector<MoveClass> vector1;
    vector1.push_back(MoveClass("mc1"));

    MoveClass moveClass1("A");
    moveClass1 = MoveClass("B");

    //vector<NoMoveClass> vector3;
    //vector3.push_back(NoMoveClass("nmc1"));
}

void sample5() {
    MoveClass moveClass1("A");
    MoveClass moveClass2("B");
    moveClass1 = moveClass2;//moveClass2是左值，使用拷贝操作
    moveClass1 = MoveClass("C");//使用移动操作，MoveClass("C") 是右值
}

//-Og -fno-elide-constructors
int main() {
    //sample1();
    //sample2();
    //sample3();
    //sample4();
    sample5();
    return EXIT_SUCCESS;
}