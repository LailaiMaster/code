#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

/**下载上下文*/
typedef struct Context {
    /**锁，用于保护 download_left*/
    mtx_t mutex;
    /**剩余多少*/
    int download_left;
} Context;

/**下载的请求*/
typedef struct DownloadRequest {
    Context *context;
    char const *url;
    char const *filename;
    /**进度*/
    int progress;
    /**模拟下载更新速度*/
    int interval;

    /**回调*/
    void (*callback)(struct DownloadRequest *);

} DownloadRequest;

/**模拟下载*/
int DownloadFile(DownloadRequest *request) {
    PRINTLNF("\rDownloading file from: %s into %s ...", request->url, request->filename);

    for (int i = 0; i < 100; ++i) {
        request->progress = i;
        SleepMs(request->interval);
    }

    request->callback(request);
    return 0;
}

/**回调*/
void DownloadCallback(DownloadRequest *request) {
    mtx_lock(&request->context->mutex);
    request->context->download_left--;

    /* \r 表示回到打印行的开头。*/
    PRINTLNF("\rDownload file from: %s into %s successfully, left: %d",
             request->url,
             request->filename,
             request->context->download_left);

    mtx_unlock(&request->context->mutex);
}

#define DOWNLOAD_TASKS 5

//=============== 处理复杂的线程返回的结果【回调】 ===============
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

    //所有的下载请求
    DownloadRequest requests[DOWNLOAD_TASKS];

    /*初始化上下文，任务数量 和 锁*/
    Context context = {.download_left = DOWNLOAD_TASKS};
    mtx_init(&context.mutex, mtx_plain);

    //初始化所有的 DownloadRequest ，并开启下载线程
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
        //释放 thrd_t 资源
        thrd_detach(t);
    }

    /*观察下载进度*/
    while (1) {
        //拿到剩余下载任务数
        mtx_lock(&context.mutex);
        int download_left = context.download_left;
        mtx_unlock(&context.mutex);
        //都完成了就退出程序
        if (download_left == 0) {
            break;
        }

        //打印每个任务的进度
        printf("\r");
        for (int i = 0; i < DOWNLOAD_TASKS; ++i) {
            printf("%s -- %3d%% \t", requests[i].filename, requests[i].progress);
        }

        //刷新缓冲区，然后休息一下
        fflush(stdout);
        SleepMs(100);
    }

    mtx_destroy(&context.mutex);
    return 0;
}