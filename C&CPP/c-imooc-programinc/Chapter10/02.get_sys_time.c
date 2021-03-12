#include <io_utils.h>
#include <time_utils.h>
#include <time.h>

//=============== 获取系统时间戳 ===============
int main() {
    //=============== 秒值 ===============
    time_t current_time;
    time(&current_time);
    PRINT_LONG(current_time);
    current_time = time(NULL);
    PRINT_LONG(current_time);

    //=============== 毫秒值 ===============
    PRINT_LLONG(TimeInMillisecond());
    PRINT_LLONG(TimeInMillisecond());
    PRINT_LLONG(TimeInMillisecond());
    PRINT_LLONG(TimeInMillisecond());

    return 0;
}
