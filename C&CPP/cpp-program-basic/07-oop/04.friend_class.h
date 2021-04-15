/*
 ============================================================================
 
 Author      : Ztiany
 Description : 友元

 ============================================================================
 */

#ifndef C_BASIC_FRIEND_CLASS_H
#define C_BASIC_FRIEND_CLASS_H

#include <iostream>
#include <vector>

//先声明Box。
class Box;

class FriendMethod {
public:
    //这里只要声明，不要定义，否则无法访问Box的成员。
    void printBoxInfo(Box &box);
};

class Box {
private:
    double width;
public:
    void setWidth(double wid);

public://友元放在一起
    friend class Friend;//友元类

    friend void FriendMethod::printBoxInfo(Box &);//友元成员函数

    friend void printWidth(Box box);//普通友元函数
};

// 成员函数定义
void Box::setWidth(double wid) {
    width = wid;
}

// 请注意：printWidth() 不是任何类的成员函数
void printWidth(Box box) {
    /* 因为 printWidth() 是 Box 的友元，它可以直接访问该类的任何成员 */
    std::cout << "Width of box : " << box.width << std::endl;
}

//这是一个友元类
class Friend {
public:
    double getBoxWidth(Box box) {
        return box.width;
    }
};

//定义友元成员函数
void FriendMethod::printBoxInfo(Box &box) {
    std::cout << "box: " << box.width << std::endl;
}

//演示友元的作用域
class FriendScope{
public:
    //可以在类中直接定义友元，但是如果想要调用该函数，还需要用普通方式再声明一次
    friend void f(){}
    FriendScope(){
        //错误的调用，f 没有被声明。
        //f();
    }

    void g();
    void h();
};

void FriendScope::g(){
    //错误的调用，f 没有被声明。
    //f();
}

void f();

void FriendScope::h(){
    f();//可以调用，因为 f 已经声明在作用域中了。
}

#endif //C_BASIC_FRIEND_CLASS_H
