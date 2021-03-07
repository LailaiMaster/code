#include <io_utils.h>
#include <time.h>

#include <sys/timeb.h>

#ifdef __unix__
#  include <sys/time.h>
#endif

int main() {
  time_t;
  clock_t;
  struct tm;
  struct timespec; // C11; MSVC
  struct timeb;
#ifdef __unix__
  struct timeval;
#endif
  return 0;
}
