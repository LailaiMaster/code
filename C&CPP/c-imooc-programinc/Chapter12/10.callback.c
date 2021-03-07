#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

typedef struct Context {
  mtx_t mutex;
  int download_left;
} Context;

typedef struct DownloadRequest {
  Context *context;
  char const *url;
  char const *filename;
  int progress;
  int interval;
  void (*callback)(struct DownloadRequest *);
} DownloadRequest;

int DownloadFile(DownloadRequest *request) {
  PRINTLNF("\rDownloading file from: %s into %s ...", request->url, request->filename);

  for (int i = 0; i < 100; ++i) {
    request->progress = i;
    SleepMs(request->interval);
  }

  request->callback(request);
  return 0;
}

void DownloadCallback(DownloadRequest *request) {
  mtx_lock(&request->context->mutex);
  request->context->download_left--;
  PRINTLNF("\rDownload file from: %s into %s successfully, left: %d",
           request->url,
           request->filename,
           request->context->download_left);

  mtx_unlock(&request->context->mutex);
}

#define DOWNLOAD_TASKS 5

int main(void) {
  char *urls[] = {
      "https://www.bennyhuo.com/file1",
      "https://www.bennyhuo.com/file2",
      "https://www.bennyhuo.com/file3",
      "https://www.bennyhuo.com/file4",
      "https://www.bennyhuo.com/file5",
  };

  char *filenames[] = {
      "download/file1",
      "download/file2",
      "download/file3",
      "download/file4",
      "download/file5",
  };

  DownloadRequest requests[DOWNLOAD_TASKS];
  Context context = {.download_left = DOWNLOAD_TASKS};
  mtx_init(&context.mutex, mtx_plain);

  for (int i = 0; i < DOWNLOAD_TASKS; ++i) {
    requests[i] = (DownloadRequest) {
      .context = &context,
      .url = urls[i],
      .filename = filenames[i],
      .progress = 0,
      .interval = i * 50 + 100,
      .callback = DownloadCallback
    };

    thrd_t t;
    thrd_create(&t, DownloadFile, &requests[i]);
    thrd_detach(t);
  }

  while(1) {
    mtx_lock(&context.mutex);
    int download_left = context.download_left;
    mtx_unlock(&context.mutex);
    if (download_left == 0) {
      break;
    }

    printf("\r");

    for (int i = 0; i < DOWNLOAD_TASKS; ++i) {
      printf("%s -- %3d%% \t", requests[i].filename, requests[i].progress);
    }

    fflush(stdout);
    SleepMs(100);
  }

  mtx_destroy(&context.mutex);
  return 0;
}