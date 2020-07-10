/*
 ============================================================================
 
 Author      : Ztiany
 Description : 静态成员

 ============================================================================
 */

#ifndef CPLUSPLUS_PROGRAM_STATIC_FILED_H
#define CPLUSPLUS_PROGRAM_STATIC_FILED_H

class Instance {
public:
    static Instance *getInstance();
private:
    static Instance *instance;
};

#endif

//Instance.cpp
#include "Instance.h"

Instance *Instance::instance = 0;

Instance *Instance::getInstance() {
    //C++11以后，编译器保证内部静态变量的线程安全性
    if (!instance) {
        instance = new Instance;
    }
    return instance;
}

#endif //CPLUSPLUS_PROGRAM_STATIC_FILED_H
