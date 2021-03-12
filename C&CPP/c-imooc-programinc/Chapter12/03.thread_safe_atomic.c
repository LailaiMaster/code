#include <io_utils.h>
#include <tinycthread.h>

int count = 0;

int Counter(void *arg) {
    PRINTLNF("Counter.");
    for (int i = 0; i < 1000000; ++i) {
        count++;
        //count ++ 的实际运行过程，这个过程不具有原子性。
        // int temp = count, count = temp + 1, temp;
    }
    return 0;
}

//=============== 线程安全问题1：原子性 ===============
int main(void) {
    thrd_t t_1, t_2;
    thrd_create(&t_1, Counter, NULL);
    thrd_create(&t_2, Counter, NULL);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    PRINT_INT(count);
}