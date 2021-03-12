#include <io_utils.h>
#include <tinycthread.h>

/**
 * 创建线程时，新线程要执行的函数体。
 *  1. 参数可以是任何类型，前提是调用方传递相同类型的参数。
 *  2. 返回值可以根据需求自行定义，不一定就是 int【其他线程可以通过 join 获取该线程的返回值】。
 */
int SayHello(char *name) {
    /*
     * thrd_current 在 windows 上的实现有问题，返回的并不在真正意义上的线程 id，而在 Ubuntu 上实现是正确的。
     */
    PRINTLNF("Run in new thread[%#x]: Hello, %s", thrd_current(), name);
    return 1;
}

//=============== 线程的基本用方法 ===============
int main(void) {
    thrd_t new_thread;
    int result = thrd_create(&new_thread, SayHello, "C lang");
    if (result == thrd_success) {
        PRINTLNF("Run in Main thread[%#x], created new_thread[%#x]", thrd_current(), new_thread);
    } else {
        PRINTLNF("Run in Main Thread[%#x], failed to create new_thread", thrd_current());
    }

    /*
     * 1. msvc 实现：主线程默认就会等子线程执行完毕。
     * 2. mingw 实现：主线程默认不会等子线程执行完毕，需要主动调用。
     */

    //线程睡眠：thrd_sleep
    /*
     thrd_sleep(
            &(struct timespec) {.tv_sec = 0, .tv_nsec = 100000000},//匿名结构体
            NULL
        );
     */

    //线程等待：thrd_join
    int res;//res 用于接收结果
    thrd_join(new_thread, &res);

    //线程让出执行权：thrd_yield()
    //thrd_yield();

    //释放线程资源：线程执行完毕，不会主动释放资源，除非调用 thrd_join 主动等待执行结果，或者如果不关心结果，就调用 thrd_detach 函数。【detach 于 join 互斥】
    //thrd_detach(new_thread);

    PRINTLNF("Run in Main Thread[%#x], result from thread[%#x]: %d", thrd_current(), new_thread, res);
}