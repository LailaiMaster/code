#include <io_utils.h>
#include <time_utils.h>
#include <time.h>

//=============== 获取日历时间 ===============
int main() {
    //=============== 获取秒 ===============
    time_t current_time;
    time(&current_time);
    PRINT_LLONG(current_time);

    //=============== 获取日历时间，时间戳转换为日历时间【localtime 采用的是机器本地的时区】 ===============
    struct tm *calendar_time = localtime(&current_time);
    PRINT_INT(calendar_time->tm_year);
    PRINT_INT(calendar_time->tm_mon);
    PRINT_INT(calendar_time->tm_mday);
    PRINT_INT(calendar_time->tm_hour);
    PRINT_INT(calendar_time->tm_min);
    PRINT_INT(calendar_time->tm_sec);

    //=============== 日历时间转换为时间戳 ===============
    calendar_time->tm_sec = 70;//改成 70，mktime 会完成进位操作。
    time_t current_time2 = mktime(calendar_time);
    PRINT_LLONG(current_time2);
    PRINT_INT(calendar_time->tm_year);
    PRINT_INT(calendar_time->tm_mon);
    PRINT_INT(calendar_time->tm_mday);
    PRINT_INT(calendar_time->tm_hour);
    PRINT_INT(calendar_time->tm_min);
    PRINT_INT(calendar_time->tm_sec);

    //=============== 获取格林威治时间 【跟北京时间差 8 小时】===============
    struct tm *gmt = gmtime(&current_time);
    PRINT_INT(gmt->tm_year);
    PRINT_INT(gmt->tm_mon);
    PRINT_INT(gmt->tm_mday);
    PRINT_INT(gmt->tm_hour);
    PRINT_INT(gmt->tm_min);
    PRINT_INT(gmt->tm_sec);
    return 0;
}
