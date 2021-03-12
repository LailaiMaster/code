#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

_Thread_local int count = 0;

int Counter(int *step) {
    for (int i = 0; i < 1000000; ++i) {
        count += *step;
    }
    PRINT_INT(count);
    return 0;
}

//=============== 并发工具：thread_local ===============
/*
 * 1. 通过 _Thread_local 关键字声明线程域变量。
 * 2. 通过 tss_create()/tss_delete() 可以申请和销毁线程私有内存。
 */
int main(void) {
    thrd_t t_1, t_2;
    int step_1 = 1, step_2 = 2;

    thrd_create(&t_1, Counter, &step_1);
    thrd_create(&t_2, Counter, &step_2);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    PRINT_INT(count);
}