#include <stdio.h>
#include <libavformat/avformat.h>

//--------------------------------------------
//ffmpeg 目录操作
//--------------------------------------------

/*
函数：
   avio_open_dir
   avio_read_dir
   avio_close_dir
   avio_free_directory_entry

结构体：
    AVIODirContext 操作目录上下文
    AVIODirEntry 目录项，用于存放文件名，文件大小等
 */

//gcc -g ffmpeg-dirs.c -o dirs.out `pkg-config --libs libavformat`
int main() {
    av_log_set_level(AV_LOG_INFO);

    int ret;

    AVIODirContext *ctx = NULL;
    AVIODirEntry *entry = NULL;
    
    ret = avio_open_dir(&ctx, "./",NULL);

    if(ret <0){
        av_log(NULL,AV_LOG_ERROR,"failed to open dir");
        return -1;
    }

    while (1){
        ret = avio_read_dir(ctx,&entry);
        if(ret <0){
                av_log(NULL,AV_LOG_ERROR,"failed to read dir");
                goto __fail;
        }
        if(!entry){
            break;
        }
        av_log(NULL,AV_LOG_INFO,"size = %ld, name = %s\n",entry->size,entry->name);
        avio_free_directory_entry(&entry);
    }
    
    __fail:
    avio_close_dir(&ctx);

    return 0;
}