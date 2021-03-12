//for opening strptime
#define _XOPEN_SOURCE

#include <io_utils.h>
#include <time_utils.h>
#include <time.h>
#include <stdbool.h>

//=============== 解析时间【字符串-->日历时间】 ===============
int main() {
    //C 语言并没有标准的函数来解析时间，不过在 unix 广泛应用的 posix 标准提供了 strptime 函数，要使用这个函数，必须声明  _XOPEN_SOURCE 宏。【WSL2】
    char *time = "2020-11-10 90:80:32.123";
    struct tm parsed_time;
    int millisecond = 0;

    bool useUnix = true;

    if (useUnix) {//unix
        char *unparsed_string = strptime(time, "%F %T", &parsed_time);
        if (unparsed_string) {//如果还有毫秒
            PRINTLNF("unparsed time %s", unparsed_string);
            sscanf(unparsed_string, ".%3d", &millisecond);//解析毫秒
        }
    } else {// msvc【没有函数支持，用 sscanf 模拟】
        sscanf(time, "%4d-%2d-%2d %2d:%2d:%2d.%3d",
               &parsed_time.tm_year,
               &parsed_time.tm_mon,
               &parsed_time.tm_mday,
               &parsed_time.tm_hour,
               &parsed_time.tm_min,
               &parsed_time.tm_sec,
               &millisecond);

        parsed_time.tm_year -= 1900;//修正年份，parsed_time.tm_year 指的是自 1900 年算起的年数。
        parsed_time.tm_mon -= 1;//修正月份。

        mktime(&parsed_time);//修正时间，自动处理进位。
    }

    PRINTLNF("parsed time:");
    PRINTLNF("------------------------------------------------------");
    PRINT_INT(parsed_time.tm_year);
    PRINT_INT(parsed_time.tm_mon);
    PRINT_INT(parsed_time.tm_mday);
    PRINT_INT(parsed_time.tm_hour);
    PRINT_INT(parsed_time.tm_min);
    PRINT_INT(parsed_time.tm_sec);
    PRINT_INT(millisecond);
    PRINTLNF("------------------------------------------------------");
    return 0;
}
