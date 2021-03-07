#ifndef CHAPTER10_INCLUDE_TIME_UTILS_H_
#define CHAPTER10_INCLUDE_TIME_UTILS_H_

#include <io_utils.h>

#if defined(_WIN32)
#include <sys/timeb.h>
#elif defined(__unix__) || defined(__APPLE__)
#include <sys/time.h>
#endif

typedef long long long_time_t;

long_time_t TimeInMillisecond(void) {
#if defined(_WIN32)
  struct timeb time_buffer;
  ftime(&time_buffer);
  return time_buffer.time * 1000LL + time_buffer.millitm;
#elif defined(__unix__) || defined(__APPLE__)
  struct timeval time_value;
  gettimeofday(&time_value, NULL);
  return time_value.tv_sec * 1000LL + time_value.tv_usec / 1000;
#elif defined(__STDC__) && __STDC_VERSION__ == 201112L
  struct timespec timespec_value;
  timespec_get(&timespec_value, TIME_UTC);
  return timespec_value.tv_sec * 1000LL + timespec_value.tv_nsec / 1000000;
#else
  time_t current_time = time(NULL);
  return current_time * 1000LL;
#endif
}

void TimeCost(char const* msg) {
  static long_time_t start_time = 0;
  if (msg && start_time != 0) {
    long_time_t current_time = TimeInMillisecond();
    PRINTLNF("%s costs: %lld", msg, current_time - start_time);
  }
  start_time = TimeInMillisecond();
}

void SleepMs(long milliseconds) {
  long seconds = milliseconds / 1000;
  long nanoseconds = (milliseconds % 1000) * 1000000L;
  thrd_sleep(&(struct timespec) {.tv_sec=seconds, .tv_nsec=nanoseconds}, NULL);
}

#endif //CHAPTER10_INCLUDE_TIME_UTILS_H_
