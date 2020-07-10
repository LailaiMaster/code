#include <stdio.h>
#include <libavutil/log.h>

//--------------------------------------------
//ffmpeg 日至系统
//--------------------------------------------

//gcc -g ffmpeg-log.c -o ffmepg-log.out -lavutil
int main() {
    //设置日至等级
    av_log_set_level(AV_LOG_DEBUG);
    //打印log
    av_log(NULL, AV_LOG_INFO, "...%s\n", "Hello World");
    return 0;
}