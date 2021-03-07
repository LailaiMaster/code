#include "utils/unit_utils.h"

#include <stdio.h>

#define KB 1024
#define MB 1048576
#define GB 1073741824
#define TB 1099511627776
#define PB 1125899906842624

#define MINUTE 60
#define HOUR 3600
#define DAY 86400

void FormatUnit(char *const dest, char const *unit, int64_t bytes) {
  if(bytes < 0) {
    sprintf(dest, "Unknown");
  } else if (bytes >= PB) {
    sprintf(dest, "%.1lfP%s", bytes * 1.0 / PB, unit);
  } else if (bytes >= TB) {
    sprintf(dest, "%.1lfT%s", bytes * 1.0 / TB, unit);
  } else if (bytes >= GB) {
    sprintf(dest, "%.1lfG%s", bytes * 1.0 / GB, unit);
  } else if (bytes >= MB) {
    sprintf(dest, "%.1lfM%s", bytes * 1.0 / MB, unit);
  } else if (bytes >= KB) {
    sprintf(dest, "%.1lfK%s", bytes * 1.0 / KB, unit);
  } else {
    sprintf(dest, "%lld%s", bytes, unit);
  }
}

void FormatByte(char *const dest, int64_t byte_count) {
  FormatUnit(dest, "B", byte_count);
}

void FormatByteRate(char *const dest, int64_t byte_rate) {
  FormatUnit(dest, "B/s", byte_rate);
}

void FormatTime(char *const dest, int total_seconds) {
  if (total_seconds >= DAY) {
    sprintf(dest, ">1d");
  } else {
    int hours = total_seconds / HOUR;
    int minutes = total_seconds % HOUR / MINUTE;
    int seconds = total_seconds % MINUTE;
    sprintf(dest, "%02d:%02d:%02d", hours, minutes, seconds);
  }
}