#include <io_utils.h>
#include <tinycthread.h>

int Counter(void *arg) {
    //不适用共享变量，纯函数。
    int local_count = 0;
    PRINTLNF("Counter.");

    for (int i = 0; i < 1000000; ++i) {
        //副作用
        local_count++;
        // int temp = count, count = temp + 1, temp;
    }
    return local_count;
}

//=============== 副作用与纯函数 ===============
/*
 * 纯函数没有线程安全问题。
 */
int main(void) {
    thrd_t t_1, t_2;

    thrd_create(&t_1, Counter, NULL);
    thrd_create(&t_2, Counter, NULL);

    int count = 0;
    int result;

    //让线程自己去加
    thrd_join(t_1, &result);
    //合并一下结果
    count += result;
    //让线程自己去加
    thrd_join(t_2, &result);
    //合并一下结果
    count += result;

    PRINT_INT(count);
}