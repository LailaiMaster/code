/*
 ============================================================================
 
 Author      : Ztiany
 Description : posix pthread

 ============================================================================
 */

#include <cstdlib>
#include <pthread.h>
#include <mutex>
#include <iostream>
#include <thread>
#include "posix-pthread.h"

using namespace std;

void *createThread_task(void *args) {
    cout << "createThread_task self = " << pthread_self() << " running" << endl;
    return 0;
}

/*
线程属性：

    线程具有属性，用 pthread_attr_t 表示
        pthread_attr_t attr;
        pthread_attr_init(&attr); //初始化 attr中为操作系统实现支持的线程所有属性的默认值
        pthread_attr_destroy(&attr);

        1 默认为非分离线程
            非分离线程：可以被其他线程操作，当 pthread_join() 函数返回时，创建的线程终止，释放自己占用的系统资源。
            分离线程：不能被其他线程操作。

            常量 PTHREAD_CREATE_DETACHED 表示分离
            常量 PTHREAD_CREATE_JOINABLE 表示非分离

        2 线程的调度策略

            设置调度策略，返回 0 表示设置成功
                pthread_attr_setschedpolicy(&attr, SCHED_FIFO);

            可选的调度策略
                SCHED_FIFO ：实时调度策略，先到先服务 一旦占用cpu则一直运行。一直运行直到有更高优先级任务到达或自己放弃。
                SCHED_RR：实时调度策略，时间轮转 系统分配一个时间段，在时间段内执行本线程

        3 优先级
            获得对应策略的最小、最大优先级
                int max = sched_get_priority_max(SCHED_FIFO);
                int min = sched_get_priority_min(SCHED_FIFO);
                sched_param param;
                param.sched_priority = max;
                pthread_attr_setschedparam(&attr, &param);


线程同步：多线程同时读写同一份共享资源的时候，可能会引起冲突。需要引入线程“同步”机制，即各位线程之间有序地对共享资源进行操作。
    锁（pthread_mutex_lock）：用于保持同步。
    条件变量（pthread_cond_t）：条件变量是线程间进行同步的一种机制，主要包括两个动作：一个线程等待"条件变量的条件成立"而挂起；另一个线程使"条件成立",从而唤醒挂起线程。
 */

//示例1：创建线程
void createThreadSample() {//出参，获取线程id
    //入参，线程的属性，可以传 null
    pthread_attr_t pthreadAttr;
    pthread_attr_init(&pthreadAttr);
    //设置为分离线程，此时 join 无效。
    pthread_attr_setdetachstate(&pthreadAttr, PTHREAD_CREATE_DETACHED);
    //设置调度策略
    pthread_attr_setschedpolicy(&pthreadAttr, SCHED_FIFO);
    //设置优先级
    struct sched_param schedParam{};
    schedParam.sched_priority = sched_get_priority_max(SCHED_FIFO);
    cout << "max priority = " << schedParam.sched_priority << endl;
    pthread_attr_setschedparam(&pthreadAttr, &schedParam);

    //创建线程
    pthread_t pid;
    pthread_create(&pid, &pthreadAttr, createThread_task, 0);
    pthread_attr_destroy(&pthreadAttr);
    //启动线程
    pthread_join(pid, 0);

    cout << "createThreadSample end self = " << pthread_self() << endl;
}

void *threadSafeTask(void *args) {
    auto *params = static_cast<ThreadMutexParams *>(args);

    pthread_mutex_lock(params->mutex_);

    if (!params->queue_->empty()) {
        cout << "threadSafeTask " << " getElement " << params->queue_->front() << endl;
        params->queue_->pop();
    } else {
        cout << "threadSafeTask  " << " getElement " << "nothing" << endl;
    }

    pthread_mutex_unlock(params->mutex_);

    return 0;
}

void threadSafeSample() {
    queue<int> queue;
    for (int i = 0; i < 5; ++i) {
        queue.push(i);
    }
    pthread_mutex_t pthreadMutex;
    pthread_mutex_init(&pthreadMutex, 0);

    ThreadMutexParams threadMutexParams(&queue, &pthreadMutex);

    for (int i = 0; i < 5; ++i) {
        pthread_t pid;
        pthread_create(&pid, 0, threadSafeTask, &threadMutexParams);
    }

    pthread_mutex_destroy(&pthreadMutex);
    system("pause");
}

void *getTask(void *args) {
    printf("getTask running\n");
    auto *sq = static_cast<SafeQueue<int> *>(args);
    while (true) {
        int i = -1;
        // 如果队列中没有数据就卡在这里
        sq->pop(i);
        if (i == -100) {
            break;
        }
        cout << "get:" << i << endl;
    }
    return 0;
}

void *putTask(void *args) {
    printf("putTask running\n");
    auto *sq = static_cast<SafeQueue<int> *>(args);
    while (true) {
        int i;
        // 将用户输入 给 i保存
        cin >> i;
        sq->push(i);
        if (i == -100) {
            break;
        }
        if (cin.fail()) { // 错误的输入
            cerr << "bad data, try again" << endl;// 警告用户
            cin.clear(iostream::goodbit);//重置状态
            cin.ignore();//清除缓冲区，否则错误的输入一直在缓冲区中
            continue;//继续
        }
    }
    return 0;
}

void safeQueueSample() {
    SafeQueue<int> sq{};
    pthread_t pidGet;
    pthread_t pidPut;
    pthread_create(&pidGet, 0, getTask, &sq);
    pthread_create(&pidPut, 0, putTask, &sq);
    pthread_join(pidGet, 0);
    pthread_join(pidPut, 0);
}

void cppThreadSample() {
    //线程，有时被称为轻量进程，是程序执行的最小单元。
    thread threadA([] {
        printf("Hello\n");
    });
    //等待线程结束再继续执行
    threadA.join();
}

int main() {
    cppThreadSample();
    //createThreadSample();
    //threadSafeSample();
    //safeQueueSample();
    return EXIT_SUCCESS;
}
