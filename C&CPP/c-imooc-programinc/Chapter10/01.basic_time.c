#include <io_utils.h>
#include <time.h>

//for struct timeb
#include <sys/timeb.h>

#ifdef __unix__
#  include <sys/time.h>
#endif

//=============== 时间的基本概念/表示时间的数据结构 ===============
/*
- UTC 是世界调和时间，是国际时间的标准，我们提及UTC时，它一定是一个确定的值，不受时区的影响。
- GMT就是格林威治时间，与UTC的时间是一致的，但我们说起GMT的时候其实指的就是零时区的时间，它现在已经不是时间标准了。
- Epoch，一般被翻译成纪元，时代，我们通常在计算机程序中使用的时间都是从UTC时间1970年1月1日0时0分0秒开始的一个整数值，这是Unix的计时方法，
        Unix系统上对C标准的扩展标准POSIX也采用了这样的规定，因此这个起始时间就被称为Unix Epoch，现今绝大多数编程语言例如Java，
        JavaScript等等都采用了Unix Epoch， Windows上的C实现也是如此。
 */
int main() {
    time_t;//从 Epoch 开始计算的时间【秒】。
    clock_t;//进程消耗处理器时间。
    struct tm;//方便人类阅读的时间。
    struct timespec; // C11 和  MSVC only。时间戳，更精准的时间。【纳秒】
    struct timeb;//如果平台不支持 timespec 也不支持 C11，但是又要求更精确的时间怎么办？其实很多平台都提供了 timeb 类型，在 Unix/MSVC 上都有，timeb 中有比较精确的时间。【毫秒】
#ifdef __unix__
    struct timeval;//unix 平台 only，可以提供更精确的时间【微秒】
#endif
    return 0;
}
