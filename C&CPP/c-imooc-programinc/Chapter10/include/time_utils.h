#ifndef CHAPTER10_INCLUDE_TIME_UTILS_H_
#define CHAPTER10_INCLUDE_TIME_UTILS_H_

#if defined(_WIN32)

#include <sys/timeb.h>//Windows 平台用 timeb

#elif defined(__unix__) || defined(__APPLE__)
#include <sys/time.h>//其他平台用 timeval
#endif

typedef long long long_time_t;

long_time_t TimeInMillisecond(void) {
#if defined(_WIN32)//为什么没有先判断 C11，因为 MinGW 中也有定义 WIN32 这个宏，而 MinGW 虽然定义了对 C11 的支持，但是对 timesepc 的支持不完整，没有 timespec_get() 函数。
    struct timeb time_buffer;
    ftime(&time_buffer);
    return time_buffer.time * 1000LL + time_buffer.millitm;
#elif defined(__unix__) || defined(__APPLE__)//Unix 和 MAC 用 timeval
    struct timeval time_value;
    gettimeofday(&time_value, NULL);
    return time_value.tv_sec * 1000LL + time_value.tv_usec / 1000;
#elif defined(__STDC__) && __STDC_VERSION__ == 201112L//否则如果有 C++，则使用 C11 的 time_spec，但是很多平台对 C11 的支持比较滞后，所以优先级低。
    struct timespec timespec_value;
    timespec_get(&timespec_value, TIME_UTC);
    return timespec_value.tv_sec * 1000LL + timespec_value.tv_nsec / 1000000;
#else//如果都不支持，就折中一下，使用秒
    time_t current_time = time(NULL);
    return current_time * 1000LL;
#endif
}

#endif //CHAPTER10_INCLUDE_TIME_UTILS_H_
