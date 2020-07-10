#include <stdio.h>
#include <libavformat/avformat.h>

//--------------------------------------------
//ffmpeg 文件操作
//--------------------------------------------

//gcc -g ffmpeg-files.c -o file.out `pkg-config --libs libavformat`
//pkg-config --libs libavformat 表示找到 libavformat 并链接此库
int main() {
    int ret;

    //重命名文件
    ret = avpriv_io_move("111.txt","222.txt");
    if(ret < 0){
        av_log(NULL,AV_LOG_ERROR,"Failed to rename file");
        return -1;
    }

    //删除文件
    ret = avpriv_io_delete("test.txt");
    if(ret < 0){
        av_log(NULL,AV_LOG_ERROR,"Failed to delete file");
        return -1;
    }
    return 0;
}