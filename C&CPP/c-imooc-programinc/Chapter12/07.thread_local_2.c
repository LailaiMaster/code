#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>
#include <stdlib.h>

/*线程私有域变量的 key，线程通过 key 进行设置或获取自己的私有变量。*/
tss_t count_key;

void MyFree(void *ptr) {
    PRINTLNF("Free %#x", ptr);
    free(ptr);
}

int Counter(int *step) {
    int *count = malloc(sizeof(int));
    *count = 0;
    //tss_set，将内存和这个 key 绑定起来。
    if (tss_set(count_key, count) == thrd_success) {
        for (int i = 0; i < 1000000; ++i) {
            *count += *step;
        }
    }
    PRINT_INT(*count);
    //如果运行期间删除了 key，则会报错。
    PRINT_INT(*((int *) tss_get(count_key)));
    return 0;
}

//=============== 案例：使用 tss_create/tss_delete ===============
/*
 * tss_create/tss_delete 类似于 Java 里面的 ThreadLocal。
 */
int main(void) {
    if (tss_create(&count_key, MyFree) == thrd_success) {

        thrd_t t_1, t_2;
        int step_1 = 1, step_2 = 2;

        thrd_create(&t_1, Counter, &step_1);
        thrd_create(&t_2, Counter, &step_2);

        /*
         * 如果调用了 tss_delete，就不会调用 tss_create 中传入的(tss_dtor_t: MyFree)，tss_dtor_t 只会在线程退出时且 key 还存在的时候才会自动调用。
         * 此时，一定要手动来管理申请的内存【比如 Counter 函数中的 malloc 的内存】。
         */
        //tss_delete(count_key);

        thrd_join(t_1, NULL);
        thrd_join(t_2, NULL);

        puts("t_1/t_2 ended.");
        PRINTLNF("count_key deleted.");
    }
}