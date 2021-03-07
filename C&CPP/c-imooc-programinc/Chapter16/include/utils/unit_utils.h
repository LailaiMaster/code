#ifndef BASICC_DOWNLOADER_UNIT_UTILS_H_
#define BASICC_DOWNLOADER_UNIT_UTILS_H_

#include <stdint.h>

void FormatByte(char *const dest, int64_t byte_count);

void FormatByteRate(char *const dest, int64_t byte_rate);

void FormatTime(char *const dest, int total_seconds);

#endif //BASICC_DOWNLOADER_UNIT_UTILS_H_
