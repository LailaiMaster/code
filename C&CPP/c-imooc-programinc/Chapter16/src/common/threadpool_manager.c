#include "common/threadpool_manager.h"
#include <threadpool.h>
#include <stdlib.h>

static threadpool_t *pool;

void InitThreadPool() {
  if (!pool) {
    pool = threadpool_create(5, MAX_QUEUE, 0);
  }
}

void ThreadPoolExecute(Runnable *runnable) {
  threadpool_add(pool, runnable->run, runnable->argument, 0);
}

void DestroyThreadPool() {
  if (pool) {
    threadpool_destroy(pool, 0);
    pool = NULL;
  }
}