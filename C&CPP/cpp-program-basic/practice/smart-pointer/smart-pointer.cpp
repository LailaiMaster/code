/*
 ============================================================================
 
 Author      : Ztiany
 Description : 智能指针

 ============================================================================
 */

#include "smart-pointer.h"
#include <memory>
#include <iostream>

using namespace std;

/*
 自C++11起，C++标准库提供了两大类型的智能指针 shared_ptr 和 unique_ptr
 */

//shared_ptr：操作引用计数实现共享式拥有的概念。多个智能指针可以指向相同的对象，这个对象和其相关资源会在最后一个被销毁时释放。
void sharedPointer() {
    auto *classA = new ClassA();
    shared_ptr<ClassA> sharedPtr1(classA);
    const shared_ptr<ClassA> &sharedPtr2 = sharedPtr1;
}

//虽然使用shared_ptr能够非常方便的为我们自动释放对象，但是还是会出现一些问题。最典型的就是循环引用问题。
void sharedPointerCycleRef() {
    //自动释放
    shared_ptr<NormalA> a(new NormalA()); //A引用计数为1
    shared_ptr<NormalB> b(new NormalB()); //B引用计数为1
    cout << a.use_count() << endl; //查看内部对象引用计数
    a->b = b;            //A 引用计数为2
    b->a = a;            //B 引用计数为2
    //退出方法，a释放，A引用计数-1结果为1 不会释放， B也一样，原因在于 a->b 和 b->a 都在堆中。
}

/*
 weak_ptr：

    weak_ptr是为配合shared_ptr而引入的一种智能指针。主要用于观测资源的引用情况。 它的构造和析构不会引起引用记数的增加或减少。
    没有重载 * 和 -> ，但可以使用 lock 方法获得一个可用的 shared_ptr 对象。weak_ptr 可以配合 shared_ptr 解决循环引用问题。

    weak_ptr 提供 expired 方法等价于 use_count == 0，当 expired 为 true 时，lock 返回一个存储空指针的 shared_ptr
*/

//unique_ptr：实现独占式引用，保证同一时间只有一个智能指针指向内部对象。
void uniquePointer() {
    auto *classA = new ClassA();
    unique_ptr<ClassA> uniquePtr1(classA);
    unique_ptr<ClassA> uniquePtr2 = std::move(uniquePtr1);
}

void weakPtr() {
    //自动释放
    shared_ptr<WeakA> a(new WeakA()); //A引用计数为1
    shared_ptr<WeakB> b(new WeakB()); //B引用计数为1
    a->b = b;            //weak_ptr 引用计数不增加
    b->a = a;            //weak_ptr 引用计数不增加
    //退出方法，A B释放
}

int main() {
    //uniquePointer();
    //sharedPointer();
    //sharedPointerCycleRef();
    weakPtr();
    return EXIT_SUCCESS;
}