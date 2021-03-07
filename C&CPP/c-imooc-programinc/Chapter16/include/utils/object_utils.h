#ifndef BASICC_DOWNLOADER_UTILS_OBJECT_UTILS_H_
#define BASICC_DOWNLOADER_UTILS_OBJECT_UTILS_H_

#include <stdlib.h>

#define CREATE_OBJECT(type, variable) type *variable = malloc(sizeof(type))

#define CREATE_OBJECT_CLEANED(type, variable) type *variable = calloc(1, sizeof(type))

#endif //BASICC_DOWNLOADER_UTILS_OBJECT_UTILS_H_
