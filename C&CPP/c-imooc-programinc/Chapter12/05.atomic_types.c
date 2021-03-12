#include <io_utils.h>
#include <tinycthread.h>
#include <stdatomic.h>
#include <time_utils.h>

atomic_int count = 0;

int Counter(void *arg) {
    PRINTLNF("Counter.");
    for (int i = 0; i < 1000000; ++i) {
        count++;

        // int temp = count, count = temp + 1, temp;
    }
    return 0;
}

//=============== 并发工具：原子类型 ===============
/*
 * C11 提供了原子类型，定义在 stdatomic 头文件中。【MSVC 不支持】
 *
 *  1. 提供了很多 atomic 类型，比如 atomic_int。
 *  2. 还提供了下面宏函数【只有宏函数才不关心数据类型，所以它们是通用的】
 *          atomic_store()
 *          atomic_load()
 *          atomic_exchange()
 *          atomic_compare_exchange_strong()
 *          atomic_compare_exchange_weak()：与 strong 相比，性能更好，但 weak 有可能出现虚假失败，即即使条件满足也可能失败，这是多试几次就好了。
 *
 *   这些原子操作通过 cpu 指令（如果 cpu 支持的话）或者锁来支持，C11 提供了一个 atomic_is_lock_free() 函数来判断是不是由 cpu 指令来支持原子性的。
 *   C11 标准没有要求一定要使用某种方式来实现原子性，只定义了一个无锁实现的 atomic_flag，其功能与 atomic_bool 类似。而对 atomic_flag 读写需要
 *   使用专门的函数 atomic_flag_clear()/atomic_flag_test_and_set() 来进行，atomic_flag 特别适用用来做标志位。
 *
 *   原子类型的操作可以保证原子性，同时也可以保证可见性，对于可见性保证，C 标准有更详细的定义，大多数原子操作函数都有 explicit 结尾的版本，允许调用者
 *   精确地控制内存序。不指定内存序版本的函数都采用最严格的方案，相对性能也最差。具体参考：
 *      <http://senlinzhan.github.io/2017/12/04/cpp-memory-order/>
 *      <http://www.cplusplus.com/reference/atomic/memory_order/>
 *
 */
int main(void) {
    TimeCost(NULL);
    thrd_t t_1, t_2;
    thrd_create(&t_1, Counter, NULL);
    thrd_create(&t_2, Counter, NULL);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    PRINT_INT(count);
    TimeCost("atomic");
}