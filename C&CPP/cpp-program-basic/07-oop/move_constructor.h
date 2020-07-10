/*
 ============================================================================
 
 Author      : Ztiany
 Description : 对象移动

 ============================================================================
 */
#include <string>
#include <iostream>

#ifndef CPLUSPLUS_PROGRAM_MOVE_CONSTRUCTOR_H
#define CPLUSPLUS_PROGRAM_MOVE_CONSTRUCTOR_H

class MoveClass {
private:
    std::string *name_;
public:

    MoveClass() : name_(nullptr) {
        std::cout << "MoveClass()" << std::endl;
    }

    explicit MoveClass(const std::string &name) : name_(new std::string(name)) {
        std::cout << name << "  MoveClass()" << std::endl;
    }

    MoveClass(const MoveClass &other) {
        if (other.name_) {
            name_ = new std::string(*other.name_);
        }
        if (name_) {
            std::cout << *name_ << "  copy MoveClass()" << std::endl;
        } else {
            std::cout << "copy MoveClass()" << std::endl;
        }
    }

    MoveClass &operator=(const MoveClass &other) {
        if (this == &other) {
            return *this;
        }
        delete name_;
        if (other.name_) {
            name_ = new std::string(*other.name_);
        }
        if (name_) {
            std::cout << *name_ << "  operator= MoveClass()" << std::endl;
        } else {
            std::cout << "operator= MoveClass()" << std::endl;
        }
        return *this;
    }

    MoveClass(MoveClass &&other) noexcept : name_(other.name_) {
        //将源对象置为可析构状态
        other.name_ = nullptr;
        if (name_) {
            std::cout << *name_ << "  move MoveClass()" << std::endl;
        } else {
            std::cout << "move MoveClass()" << std::endl;
        }
    }

    MoveClass &operator=(MoveClass &&other) noexcept {
        if (this == &other) {
            return *this;
        }
        //使用已有的资源
        delete name_;
        //执行资源拷贝
        if (other.name_) {
            name_ = other.name_;
        }
        //将源对象置为可析构状态
        other.name_ = nullptr;
        if (name_) {
            std::cout << *name_ << "  operator= move MoveClass()" << std::endl;
        } else {
            std::cout << "operator= move MoveClass()" << std::endl;
        }
        return *this;
    }

    ~MoveClass() {
        if (name_) {
            std::cout << *name_ << "  ~" << "MoveClass()" << std::endl;
        } else {
            std::cout << "~" << "MoveClass()" << std::endl;
        }
        delete name_;
    }
};

class NoMoveClass {
private:
    std::string *name_;
public:

    NoMoveClass() : name_(nullptr) {
        std::cout << "NoMoveClass()" << std::endl;
    }

    explicit NoMoveClass(const std::string &name) : name_(new std::string(name)) {
        std::cout << name << "  NoMoveClass()" << std::endl;
    }

    NoMoveClass(const NoMoveClass &other) {
        if (other.name_) {
            name_ = new std::string(*other.name_);
        }

        if (name_) {
            std::cout << *name_ << "  copy NoMoveClass()" << std::endl;
        } else {
            std::cout << "copy NoMoveClass()" << std::endl;
        }
    }

    NoMoveClass &operator=(const NoMoveClass &other) {
        if (this == &other) {
            return *this;
        }
        delete name_;
        if (other.name_) {
            name_ = new std::string(*other.name_);
        }
        if (name_) {
            std::cout << *name_ << "  operator= NoMoveClass()" << std::endl;
        } else {
            std::cout << "operator= NoMoveClass()" << std::endl;
        }
        return *this;
    }

    ~NoMoveClass() {
        if (name_) {
            std::cout << *name_ << "  ~" << "NoMoveClass()" << std::endl;
        } else {
            std::cout << "~" << "NoMoveClass()" << std::endl;
        }
        delete name_;
    }
};

#endif //CPLUSPLUS_PROGRAM_MOVE_CONSTRUCTOR_H
