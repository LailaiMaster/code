#ifndef BASICC_DOWNLOADER_THREADPOOL_MANAGER_H_
#define BASICC_DOWNLOADER_THREADPOOL_MANAGER_H_

#define RUNNABLE(run_func, arg) &(Runnable) {.run=(RunFunc) run_func, .argument=arg}

typedef void (*RunFunc)(void *);

typedef struct Runnable {
  RunFunc run;
  void *argument;
} Runnable;

/**
 * Call this on glib main thread.
 */
void InitThreadPool();

/**
 * Call this on glib main thread.
 */
void DestroyThreadPool();

/**
 * Thread safe. You can call this after init before destroy.
 * @param runnable
 */
void ThreadPoolExecute(Runnable *runnable);

#endif //BASICC_DOWNLOADER_THREADPOOL_MANAGER_H_
