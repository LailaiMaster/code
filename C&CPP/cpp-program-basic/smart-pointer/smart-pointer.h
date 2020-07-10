/*
 ============================================================================
 
 Author      : Ztiany
 Description : 智能指针

 ============================================================================
 */
#ifndef CPLUSPLUS_PROGRAM_SMART_POINTER_H
#define CPLUSPLUS_PROGRAM_SMART_POINTER_H

#include "cstdio"
#include <iostream>
#include <memory>

class ClassA {
private:
    int *count;
public:
    ClassA() : count(new int) {
        printf("ClassA\n");
    }

    ~ClassA() {
        printf("~ClassA\n");
        delete count;
    }
};

class NormalB;

class NormalA {
public:
    NormalA() {
        std::cout << "create NormalA" << std::endl;
    }

    ~NormalA() {
        std::cout << "delete NormalA" << std::endl;
    }

    std::shared_ptr<NormalB> b;
};

class NormalB {
public:
    NormalB() {
        std::cout << "create NormalB" << std::endl;
    }

    ~NormalB() {
        std::cout << "deleteNormal B" << std::endl;
    }

    std::shared_ptr<NormalA> a;
};

class WeakB;

class WeakA {
public:
    WeakA() {
        std::cout << "create WeakA" << std::endl;
    }

    ~WeakA() {
        std::cout << "delete WeakA" << std::endl;
    }

    std::weak_ptr<WeakB> b;
};

class WeakB {
public:
    WeakB() {
        std::cout << "create WeakB" << std::endl;
    }

    ~WeakB() {
        std::cout << "delete WeakB" << std::endl;
    }

    std::weak_ptr<WeakA> a;
};

#endif //CPLUSPLUS_PROGRAM_SMART_POINTER_H
