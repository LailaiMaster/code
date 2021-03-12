#include <io_utils.h>
#include <time_utils.h>
#include <time.h>

#define PI 3.1415926

void DoHardWork() {
    double sum = 0;
    for (int i = 0; i < 10000000; ++i) {
        sum += i * i / PI;
    }
    PRINT_DOUBLE(sum);
}

//=============== 计算时间差 ===============
int main() {
    //=============== 计算时间差：difftime 【秒】===============
    time_t start_time = time(NULL);
    DoHardWork();
    time_t end_time = time(NULL);
    double diff = difftime(start_time, end_time);
    PRINT_DOUBLE(diff);

    //=============== 计算时间差： 【毫秒】===============
    long_time_t start_time_ms = TimeInMillisecond();
    DoHardWork();
    long_time_t end_time_ms = TimeInMillisecond();
    PRINT_LLONG(end_time_ms - start_time_ms);

    //=============== 计算时间差：处理器时间===============
    //clock 返回的是处理器消耗的时间，更能真实地反应程序运行的耗时。
    //time 返回的是系统时间，更适合描述真实世界的时间变化。
    clock_t start_time_c = clock();
    DoHardWork();
    clock_t end_time_c = clock();
    //打印的是 CPU 时钟数
    PRINT_INT((end_time_c - start_time_c));
    //CLOCKS_PER_SEC 表示每一秒有多少 CPU 时钟，下面打印的是秒数
    PRINT_DOUBLE((end_time_c - start_time_c) * 1.0 / CLOCKS_PER_SEC);
    return 0;
}
