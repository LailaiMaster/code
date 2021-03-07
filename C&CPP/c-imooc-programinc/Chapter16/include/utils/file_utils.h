#ifndef BASICC_DOWNLOADER_FILE_UTILS_H_
#define BASICC_DOWNLOADER_FILE_UTILS_H_

#include <stdio.h>

FILE *SmartFOpen(char const *filename, char const *mode);

FILE *SmartFOpenFileInDirectory(const char *directory, const char *filename, const char *mode);

void JoinPath(char *destination, const char *path1, const char *path2);

#endif //BASICC_DOWNLOADER_FILE_UTILS_H_
