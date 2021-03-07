#ifndef BASICC_INCLUDE_COMMON_HTTP_COMMON_H_
#define BASICC_INCLUDE_COMMON_HTTP_COMMON_H_

#include <stdint.h>

typedef void (*OnRequestProgress)(void *receiver, uint64_t current_bytes, uint64_t total_bytes);

typedef void (*OnRequestError)(void *receiver, int code, char const *message);

typedef void (*OnRequestSuccess)(void *receiver, void *data);

typedef void (*OnRequestCancellation)(void *receiver);

#endif //BASICC_INCLUDE_COMMON_HTTP_COMMON_H_
