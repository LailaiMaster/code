#include <io_utils.h>
#include <time_utils.h>
#include <time.h>

//=============== 格式化时间 ===============
int main() {
    long_time_t current_time_in_ms = TimeInMillisecond();
    int current_time_millisecond = current_time_in_ms % 1000;
    time_t current_time;
    time(&current_time);
    PRINT_LLONG(current_time);

    struct tm *calendar_time = localtime(&current_time);
    PRINT_INT(calendar_time->tm_year);
    PRINT_INT(calendar_time->tm_mon);
    PRINT_INT(calendar_time->tm_mday);
    PRINT_INT(calendar_time->tm_hour);
    PRINT_INT(calendar_time->tm_min);
    PRINT_INT(calendar_time->tm_sec);

    //=============== 格式化时间：asctime【使用的共享内存，不支持并发】 ===============
    puts(asctime(calendar_time));
    //=============== 格式化时间：ctime【使用的共享内存，不支持并发】 ===============
    puts(ctime(&current_time));

    //=============== 格式化时间：strftime ===============
    //2020-11-09 06:59:47【20个字符】
    char current_time_s[20];
    //size_t size = strftime(current_time_s, 20, "%Y-%m-%d %H:%M:%S", calendar_time);//方式 1
    size_t size = strftime(current_time_s, 20, "%F %T", calendar_time);//方式 2，%F = %Y-%m-%d，%T = %H:%M:%S。
    PRINT_INT(size);
    puts(current_time_s);

    //=============== 使用时间生成文件名，但是 c 语言不支持格式化毫秒，所以需要自己拼接毫秒值 ===============
    //20201109070456【14个字符】
    size_t size2 = strftime(current_time_s, 20, "%Y%m%d%H%M%S", calendar_time);
    sprintf(current_time_s + 14/* +14 表示从 14 个字符追加*/, "%03d", current_time_millisecond);
    PRINT_INT(size2);
    puts(current_time_s);
    return 0;
}
