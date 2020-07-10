#ifndef DNRECORDER_SAFE_QUEUE_H
#define DNRECORDER_SAFE_QUEUE_H


//todo 宏开关 是否使用c++11
//#define C11
#ifdef C11
#include <thread>
#endif

#include <pthread.h>
#include <string>
#include <queue>

using namespace std;

template<typename T>
class SafeQueue {
    typedef void (*ReleaseCallback)(T *);

    typedef void (*SyncHandle)(queue<T> &);

public:
    SafeQueue() {
#ifdef C11

#else
        pthread_mutex_init(&mutex, NULL);
        pthread_cond_init(&cond, NULL);
#endif

    }

    ~SafeQueue() {
#ifdef C11
#else
        pthread_cond_destroy(&cond);
        pthread_mutex_destroy(&mutex);
#endif

    }

    void push(const T new_value) {
#ifdef C11
        //锁 和智能指针原理类似，自动释放
        lock_guard<mutex> lk(mt);
        if (work) {
            q.push(new_value);
            cv.notify_one();
        }
#else
        pthread_mutex_lock(&mutex);
        if (work) {
            q.push(new_value);
            pthread_cond_signal(&cond);
            pthread_mutex_unlock(&mutex);
        }
        pthread_mutex_unlock(&mutex);
#endif

    }

    int pop(T &value) {
        int ret = 0;
#ifdef C11
        //占用空间相对lock_guard 更大一点且相对更慢一点，但是配合条件必须使用它，更灵活
        unique_lock<mutex> lk(mt);
        //第二个参数 lambda表达式：false则不阻塞 往下走
        cv.wait(lk,[this]{return !work || !q.empty();});
        if (!q.empty()) {
            value = q.front();
            q.pop();
            ret = 1;
        }
#else
        pthread_mutex_lock(&mutex);
        //在多核处理器下，由于竞争可能虚假唤醒，包括jdk也说明了
        while (work && q.empty()) {
            pthread_cond_wait(&cond, &mutex);
        }
        if (!q.empty()) {
            value = q.front();
            q.pop();
            ret = 1;
        }
        pthread_mutex_unlock(&mutex);
#endif
        return ret;
    }

    void setWork(int work) {
#ifdef C11
        lock_guard<mutex> lk(mt);
        this->work = work;
#else
        pthread_mutex_lock(&mutex);
        this->work = work;
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
#endif

    }

    int empty() {
        return q.empty();
    }

    int size() {
        return q.size();
    }

    void clear() {
#ifdef C11
        lock_guard<mutex> lk(mt);
        int size = q.size();
        for (int i = 0; i < size; ++i) {
            T value = q.front();
            releaseHandle(value);
            q.pop();
        }
#else
        pthread_mutex_lock(&mutex);
        int size = q.size();
        for (int i = 0; i < size; ++i) {
            T value = q.front();
            releaseCallback(&value);
            q.pop();
        }
        pthread_mutex_unlock(&mutex);
#endif

    }

    void sync() {
#ifdef C11
        lock_guard<mutex> lk(mt);
        syncHandle(q);
#else
        pthread_mutex_lock(&mutex);
        //同步代码块当我们调用sync方法的时候，能够保证是在同步块中操作queue 队列。
        //方便主动丢包：直播的时候，可能有延迟，延迟达到一定程度后，就要来丢包，这需要在同步地操作。
        syncHandle(q);
        pthread_mutex_unlock(&mutex);
#endif

    }

    void setReleaseCallback(ReleaseCallback r) {
        releaseCallback = r;
    }

    void setSyncHandle(SyncHandle s) {
        syncHandle = s;
    }

private:

#ifdef C11
    mutex mt;
    condition_variable cv;
#else
    pthread_cond_t cond;
    pthread_mutex_t mutex;
#endif

    queue<T> q;
    /*
     * 是否工作的标记 1 ：不接收数据 ；0：不接受数据。
     * 为什么要加标记，这涉及到线程同步的问题，方便控制数据接受。
     */
    int work;
    ReleaseCallback releaseCallback;
    SyncHandle syncHandle;

};


#endif //DNRECORDER_SAFE_QUEUE_H
