#include <string>

#include <cstddef>

class HasPtr {
public:
    // 构建一个新的对象，use 设置为 1
    HasPtr(const std::string &s = std::string()) : ps(new std::string(s)), i(0), use(new std::size_t(1)) {}

    // copy constructor copies all three data members and increments the counter
    //复制构造函数复制所有的成员，然后计数加 1.
    HasPtr(const HasPtr &p) : ps(p.ps), i(p.i), use(p.use) { ++*use; }

    HasPtr &operator=(const HasPtr &);

    ~HasPtr();

    // move constructor takes over the pointers from its argument and makes the argument safe to delete
    //移动构造函数接手产生中的值，然后确认参数安全的删除。
    HasPtr(HasPtr &&p) : ps(p.ps), i(p.i), use(p.use) {
        p.ps = 0;
        p.use = 0;
    }

    HasPtr &operator=(HasPtr &&);

private:
    std::string *ps;
    int i;
    //用于跟踪多个对象共享了 ps 指针
    std::size_t *use;
};

HasPtr::~HasPtr() {
    if (--*use == 0) {   // if the reference count goes to 0，如果计数变为0，删除指针成员
        delete ps;       // delete the string
        delete use;      // and the counter
    }
}

HasPtr &HasPtr::operator=(HasPtr &&rhs) {
    if (this != &rhs) {
        if (--*use == 0) {   // do the work of the destructor，执行解构工作。
            delete ps;
            delete use;
        }
        ps = rhs.ps;         // do the work of the move constructor，执行移动构造函数的工作。
        i = rhs.i;
        use = rhs.use;
        ps = 0;
        use = 0;
    }
    return *this;
}

HasPtr &HasPtr::operator=(const HasPtr &rhs) {
    ++*rhs.use;  // increment the use count of the right-hand operand
    //该对象中的内容将被重新赋值，检测旧的内容是否需要被释放。
    if (--*use == 0) {   // then decrement this object's counter
        delete ps;       // if no other users
        delete use;      // free this object's allocated members
    }
    ps = rhs.ps;         // copy data from rhs into this object
    i = rhs.i;
    use = rhs.use;
    return *this;        // return this object
}

HasPtr f(HasPtr hp) { // HasPtr passed by value, so it is copied
    HasPtr ret;
    ret = hp;        // assignment copies the given HasPtr
    // proces ret
    return ret;      // ret and hp are destroyed
}

int main() {
    HasPtr h("hi mom!");
    HasPtr h2 = h;  // no new dynamic_memory is allocated,
    // h and h2 share the same underlying string
} 

