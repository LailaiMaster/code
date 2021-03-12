/*
 ============================================================================
 
 Author      : Ztiany
 Description : 线程自动化管理封装

 ============================================================================
 */

#ifndef CPLUSPLUS_PROGRAM_THREADLOCK_H
#define CPLUSPLUS_PROGRAM_THREADLOCK_H

#include <mutex>
#include <pthread.h>
#include <cassert>
#include <sys/file.h>

class ThreadLock {
private:
    pthread_mutex_t m_lock;
public:
    ThreadLock() {
        //初始化锁的属性
        pthread_mutexattr_t mutexattr;
        pthread_mutexattr_init(&mutexattr);
        pthread_mutexattr_settype(&mutexattr, PTHREAD_MUTEX_RECURSIVE);

        //初始化锁
        pthread_mutex_init(&m_lock, &mutexattr);

        //释放资源
        pthread_mutexattr_destroy(&mutexattr);
    }

    ~ThreadLock() {
        pthread_mutex_destroy(&m_lock);
    }

    void lock() {
        auto result = pthread_mutex_lock(&m_lock);
        if (result != 0) {
            printf("ThreadLock lock error\n");
        }
    }

    bool try_lock() {
        auto result = pthread_mutex_trylock(&m_lock);
        return result == 0;
    }

    void unlock() {
        auto result = pthread_mutex_unlock(&m_lock);
        if (result != 0) {
            printf("ThreadLock unlock error\n");
        }
    }
};

template<typename T>
class ScopedLock {
private:
    T *m_lock;
public:

    //禁止使用编译器默认生成的函数
    ScopedLock(const ScopedLock<T> &other) = delete;

    //禁止默认拷贝构造与=赋值
    ScopedLock &operator=(const ScopedLock<T> &other) = delete;

    ScopedLock(T *llock) : m_lock(llock) {
        assert(m_lock);
        lock();
    }

    ~ScopedLock() {
        unlock();
    }

    void lock() {
        if (m_lock) {
            m_lock->lock();
        }
    }

    bool try_lock() {
        if (m_lock) {
            return m_lock->try_lock();
        }
        return false;
    }

    void unlock() {
        if (m_lock) {
            m_lock->unlock();
        }
    }

};

//decltype：推断变量类型；__scopedLock##counter(&lock)：##为连接符
#define __SCOPEDLOCK(lock, counter) ScopedLock<decltype(lock)> __scopedLock##counter(&lock)

//宏函数 __COUNTER__：初始值为0，编译单元内每出现一次出现该宏,便会加1。
#define _SCOPEDLOCK(lock, counter) __SCOPEDLOCK(lock, counter)

#define SCOPEDLOCK(lock) _SCOPEDLOCK(lock, __COUNTER__)

#endif //CPLUSPLUS_PROGRAM_THREADLOCK_H
