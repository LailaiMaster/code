/*
 ============================================================================
 
 Author      : Ztiany
 Description : 智能指针

 ============================================================================
 */

#include "02-smart-ptr.h"
#include <cstdlib>

using namespace std;

void usingSmartPtr1() {
    smart_ptr1<Shape> smartPtr1(new Shape());
    smart_ptr1<Shape> smartPtr2;
    smartPtr1.get()->name();
}

void usingSmartPtr2() {
    smart_ptr2<Shape> smartPtr2(new Shape());
    smartPtr2->name();
}

void usingSmartPtr3() {
    smart_ptr3<Shape> smartPtr3_1(new Shape("Shape1"));
    smart_ptr3<Shape> smartPtr3_2(new Shape("Shape2"));
    smartPtr3_1 = smartPtr3_2;
}

void usingSmartPtr4_1() {
    smart_ptr4<Shape> ptr1{new Shape("Shape4")};
    //smart_ptr4<Shape> ptr2{ptr1};             //  编译出错
    smart_ptr4<Shape> ptr3;
    //ptr3 = ptr1;                             //  编译出错
    ptr3 = std::move(ptr1);                  // OK，可以
    smart_ptr4<Shape> ptr4{std::move(ptr3)};  // OK，可以
}

void usingSmartPtr4_2() {
    smart_ptr4<Circle> ptr1{new Circle("Circle")};
    smart_ptr4<Shape> ptr2{std::move(ptr1)};  // OK，可以
    //smart_ptr4<Shape> ptr3{ptr2};             //  编译出错
}

void usingSmartPtrRefCounting() {
    smart_ptr<Circle> ptr1(new Circle());
    printf("use count of ptr1 is %ld\n", ptr1.use_count());

    smart_ptr<Shape> ptr2;
    printf("use count of ptr2 was %ld\n", ptr2.use_count());

    ptr2 = ptr1;
    printf("use count of ptr2 is now %ld\n", ptr2.use_count());
    if (ptr1) {
        puts("ptr1 is not empty");
    }

    smart_ptr<Shape> ptr3 = dynamic_pointer_cast<Shape>(ptr1);
    printf("use count of ptr3 is %ld\n", ptr3.use_count());
}

int main() {
    usingSmartPtr1();
    //usingSmartPtr2();
    //usingSmartPtr3();
    //usingSmartPtr4_1();
    //usingSmartPtr4_2();
    usingSmartPtrRefCounting();
    return EXIT_SUCCESS;
}
