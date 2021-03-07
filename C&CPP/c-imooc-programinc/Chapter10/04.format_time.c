#include <io_utils.h>
#include <time_utils.h>
#include <time.h>

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

  puts(asctime(calendar_time));
  puts(ctime(&current_time));

  //2020-11-09 06:59:47
  char current_time_s[20];
//  size_t size = strftime(current_time_s, 20, "%Y-%m-%d %H:%M:%S", calendar_time);
  size_t size = strftime(current_time_s, 20, "%F %T", calendar_time);
  PRINT_INT(size);
  puts(current_time_s);

  //20201109070456
  size_t size2 = strftime(current_time_s, 20, "%Y%m%d%H%M%S", calendar_time);
  sprintf(current_time_s + 14, "%03d", current_time_millisecond);
  PRINT_INT(size2);
  puts(current_time_s);
  return 0;
}
