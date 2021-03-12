#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

int count = 0;
mtx_t mutex;

int Counter(void *arg) {
    PRINTLNF("Counter.");
    for (int i = 0; i < 1000000; ++i) {
        mtx_lock(&mutex);
        count++;
        mtx_unlock(&mutex);
    }
    return 0;
}

//=============== 并发工具：锁 ===============
/*
 * C11 标准库中提供的 mtx_t 来实现加锁，但并没有规定如何实现它。
 *
 * 相关函数:
 *      1. mtx_init()
 *      2. mtx_destroy()
 *      3. mtx_lock()
 *      4. mtx_unlock()
 *      5. mtx_timedlock()
 *      6. mtx_trylock()
 *
 *  mtx_init() 需要一个 type 参数，type 的类型可以是
 *      mtx_plain：普通的锁【不支持重入】
 *      mtx_timed：支持超时
 *      mtx_recursive：支持重入
 *
 * 除了 mtx_t 锁，C11 还提供了 condition 用于线程间通信的条件变量。
 */
int main(void) {
    mtx_init(&mutex, mtx_plain);
    TimeCost(NULL);

    thrd_t t_1, t_2;
    thrd_create(&t_1, Counter, NULL);
    thrd_create(&t_2, Counter, NULL);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    PRINT_INT(count);
    TimeCost("lock");
    mtx_destroy(&mutex);
}