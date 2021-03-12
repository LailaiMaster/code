/*
 ============================================================================
 
 Author      : Ztiany
 Description : 智能指针

 ============================================================================
 */
#include <string>
#include <iostream>
#include <utility>  // std::swap

#ifndef CPLUSPLUS_PROGRAM_02_SMART_PTR_H
#define CPLUSPLUS_PROGRAM_02_SMART_PTR_H

class Shape {
private:
    std::string name_;
public:
    std::string name() const {
        return name_;
    }

    explicit Shape(const char *name = "Shape") : name_(name) {
        std::cout << name_ << "()" << std::endl;
    }

    ~Shape() {
        std::cout << "~" << name_ << "()" << std::endl;
    }
};

class Circle : public Shape {
public:
    explicit Circle(const char *name = "Circle") : Shape(name) {}
};

///模板通用化
template<typename T>
class smart_ptr1 {
public:
    explicit smart_ptr1(T *ptr = nullptr) : ptr_(ptr) {}

    ~smart_ptr1() { delete ptr_; }

    T *get() const { return ptr_; }

private:
    T *ptr_;
};

///行为指针化
template<typename T>
class smart_ptr2 {
public:
    explicit smart_ptr2(T *ptr = nullptr) : ptr_(ptr) {}

    ~smart_ptr2() { delete ptr_; }

    T *get() const { return ptr_; }

    T &operator*() const { return *ptr_; }

    T *operator->() const { return ptr_; }

    operator bool() const { return ptr_; }

private:
    T *ptr_;
};


///在拷贝时转移指针的所有权
template<typename T>
class smart_ptr3 {
private:
    T *ptr_;

    //使用并返回智能指针所持有的指针
    T *release() {
        T *ptr = ptr_;
        ptr_ = nullptr;
        return ptr;
    }

    //交换 this 和 rsh 所有的指针
    void swap(smart_ptr3 &rhs) {
        using std::swap;
        swap(ptr_, rhs.ptr_);
    }

public:
    explicit smart_ptr3(T *ptr = nullptr) : ptr_(ptr) {}

    ///使用一个 smart_ptr3 构造一个新的智能指针时，原 smart_ptr3 持有的指针将会失效
    smart_ptr3(smart_ptr3 &other) {
        ptr_ = other.release();
    }

    ///使用一个 smart_ptr3 给另一个 smart_ptr3 赋值时，交换它们所有的指针
    smart_ptr3 &operator=(smart_ptr3 &rhs) {
        smart_ptr3(rhs).swap(*this);
        return *this;
    }

    ~smart_ptr3() { delete ptr_; }

    T *get() const { return ptr_; }

    T &operator*() const { return *ptr_; }

    T *operator->() const { return ptr_; }

    explicit operator bool() const { return ptr_; }

};


///使用“移动”来改善 smart_ptr 行为
template<typename T>
class smart_ptr4 {
private:
    T *ptr_;

    //交换 this 和 rsh 所有的指针
    void swap(smart_ptr4 &rhs) {
        using std::swap;
        swap(ptr_, rhs.ptr_);
    }

public:
    //使用并返回智能指针所持有的指针
    T *release() {
        T *ptr = ptr_;
        ptr_ = nullptr;
        return ptr;
    }

    explicit smart_ptr4(T *ptr = nullptr) : ptr_(ptr) {
        std::cout << "smart_ptr4" << std::endl;
    }

//-----------------------------------改动处
    ///移动构造函数
    smart_ptr4(smart_ptr4 &&other) {
        ptr_ = other.release();
        std::cout << "move smart_ptr4" << std::endl;
    }

    ///赋值操作符
    smart_ptr4 &operator=(smart_ptr4 rhs) {
        rhs.swap(*this);
        std::cout << "operator= smart_ptr4" << std::endl;
        return *this;
    }

    template<typename U>
    smart_ptr4(smart_ptr4<U> &&other) {
        ptr_ = other.release();
    }
//-----------------------------------改动处

    ~smart_ptr4() { delete ptr_; }

    T *get() const { return ptr_; }

    T &operator*() const { return *ptr_; }

    T *operator->() const { return ptr_; }

    explicit operator bool() const { return ptr_; }

};


///引用计数
class shared_count {
public:
    shared_count() noexcept : count_(1) {}

    void add_count() noexcept {
        ++count_;
    }

    long reduce_count() noexcept {
        return --count_;
    }

    long get_count() const noexcept {
        return count_;
    }

private:
    long count_;
};

///智能指针，引用计数实现
template<typename T>
class smart_ptr {

private:
    T *ptr_;
    shared_count *shared_count_;

public:
    //模板的各个实例间并不天然就有 friend 关系，因而不能互访私有成员 ptr_ 和 shared_count_。我们需要在 smart_ptr 的定义中显式声明
    template<typename U>
    friend class smart_ptr;

    explicit smart_ptr(T *ptr = nullptr) : ptr_(ptr) {
        if (ptr) {
            shared_count_ = new shared_count();
        }
    }

    ~smart_ptr() {
        if (ptr_ && !shared_count_->reduce_count()) {
            delete ptr_;
            delete shared_count_;
        }
    }

    smart_ptr(const smart_ptr &other) {
        ptr_ = other.ptr_;
        if (ptr_) {
            other.shared_count_->add_count();
            shared_count_ = other.shared_count_;
        }
    }

    template<typename U>
    smart_ptr(const smart_ptr<U> &other) noexcept {
        ptr_ = other.ptr_;
        if (ptr_) {
            other.shared_count_->add_count();
            shared_count_ = other.shared_count_;
        }
    }

    template<typename U>
    smart_ptr(smart_ptr<U> &&other) noexcept {
        ptr_ = other.ptr_;
        if (ptr_) {
            shared_count_ = other.shared_count_;
            other.ptr_ = nullptr;
        }
    }

    //但为了实现这些转换，我们需要添加构造函数，允许在对智能指针内部的指针对象赋值时，使用一个现有的智能指针的共享计数。
    template<typename U>
    smart_ptr(const smart_ptr<U> &other, T *ptr) noexcept {
        ptr_ = ptr;
        if (ptr_) {
            other.shared_count_->add_count();
            shared_count_ = other.shared_count_;
        }
    }

    smart_ptr &operator=(smart_ptr rhs) noexcept {
        rhs.swap(*this);
        return *this;
    }

    T *get() const noexcept {
        return ptr_;
    }

    long use_count() const noexcept {
        if (ptr_) {
            return shared_count_->get_count();
        } else {
            return 0;
        }
    }

    void swap(smart_ptr &rhs) noexcept {
        using std::swap;
        swap(ptr_, rhs.ptr_);
        swap(shared_count_, rhs.shared_count_);
    }

    T &operator*() const noexcept {
        return *ptr_;
    }

    T *operator->() const noexcept {
        return ptr_;
    }

    operator bool() const noexcept {
        return ptr_;
    }

};

template<typename T>
void swap(smart_ptr<T> &lhs, smart_ptr<T> &rhs) noexcept {
    lhs.swap(rhs);
}

template<typename T, typename U>
smart_ptr<T> static_pointer_cast(const smart_ptr<U> &other) noexcept {
    T *ptr = static_cast<T *>(other.get());
    return smart_ptr<T>(other, ptr);
}

template<typename T, typename U>
smart_ptr<T> reinterpret_pointer_cast(const smart_ptr<U> &other) noexcept {
    T *ptr = reinterpret_cast<T *>(other.get());
    return smart_ptr<T>(other, ptr);
}

template<typename T, typename U>
smart_ptr<T> const_pointer_cast(const smart_ptr<U> &other) noexcept {
    T *ptr = const_cast<T *>(other.get());
    return smart_ptr<T>(other, ptr);
}

template<typename T, typename U>
smart_ptr<T> dynamic_pointer_cast(const smart_ptr<U> &other) noexcept {
    T *ptr = dynamic_cast<T *>(other.get());
    return smart_ptr<T>(other, ptr);
}

#endif //CPLUSPLUS_PROGRAM_02_SMART_PTR_H
