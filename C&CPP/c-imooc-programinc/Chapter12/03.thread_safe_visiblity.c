#include <io_utils.h>
#include <tinycthread.h>

int flag = 0;

int a = 0;
int x = 0;

int T1(void *arg) {
    a = 2;
    flag = 1;
    return 0;
}

int T2(void *arg) {
    while (!flag) {}
    x = a * a;
    return 0;
}

//=============== 线程安全问题2：可见性 ===============
int main(void) {
    thrd_t t_1, t_2;
    thrd_create(&t_1, T1, NULL);
    thrd_create(&t_2, T2, NULL);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    PRINT_INT(x);
}