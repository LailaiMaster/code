#ifndef BASICC_INCLUDE_UTILS_TIME_UTILS_H_
#define BASICC_INCLUDE_UTILS_TIME_UTILS_H_

#include <utils/io_utils.h>
#include <tinycthread.h>

typedef long long long_time_t;

long_time_t TimeInMillisecond(void);

void TimeCost(char const* msg);

void SleepMs(long milliseconds);

#endif //BASICC_INCLUDE_UTILS_TIME_UTILS_H_
