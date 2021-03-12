#include <io_utils.h>
#include <tinycthread.h>
#include <stdatomic.h>

atomic_flag resume_flag = ATOMIC_FLAG_INIT;

int PrintNumbers(void *arg) {
    int current = 0;
    while (atomic_flag_test_and_set(&resume_flag)) {
        current++;
        PRINT_INT(current);
        thrd_sleep(&(struct timespec) {.tv_sec=1}, NULL);
    }
    return current;
}

void run_sample() {
    atomic_flag_test_and_set(&resume_flag);//设置标志位 positive

    thrd_t t;
    thrd_create(&t, PrintNumbers, NULL);

    thrd_sleep(&(struct timespec) {.tv_sec=5}, NULL);

    atomic_flag_clear(&resume_flag);//设置标志位 negative，之后 atomic_flag_test_and_set 返回 false
    int last_number;
    thrd_join(t, &last_number);
    PRINT_INT(last_number);
}

/*
 打印结果：
        false
        true
        false
        true
 */
void test_atomic_flag() {
    int result = atomic_flag_test_and_set(&resume_flag);//第一次设置，返回 false
    if (result) {
        PRINTLNF("true");
    } else {
        PRINTLNF("false");
    }

    result = atomic_flag_test_and_set(&resume_flag);//之后的多次设置，返回 true
    if (result) {
        PRINTLNF("true");
    } else {
        PRINTLNF("false");
    }

    atomic_flag_clear(&resume_flag);//清除状态

    result = atomic_flag_test_and_set(&resume_flag);//第一次设置，返回 false
    if (result) {
        PRINTLNF("true");
    } else {
        PRINTLNF("false");
    }

    result = atomic_flag_test_and_set(&resume_flag);//之后的多次设置，返回 true
    if (result) {
        PRINTLNF("true");
    } else {
        PRINTLNF("false");
    }
}

//=============== 案例：使用 atomic_flag 来做标志位 ===============
int main(void) {
    test_atomic_flag();
    //run_sample();
    return 0;
}