#ifndef BASICC_INCLUDE_UTILS_THREAD_UTILS_H_
#define BASICC_INCLUDE_UTILS_THREAD_UTILS_H_

#ifdef __WIN32
#include <processthreadsapi.h>
#define GetCurrentThreadId() GetCurrentThreadId()
#else
#include <tinycthread.h>
#define GetCurrentThreadId() thrd_current()
#endif

#endif //BASICC_INCLUDE_UTILS_THREAD_UTILS_H_
