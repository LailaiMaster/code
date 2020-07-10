#include <libavformat/avformat.h>
#include <libavutil/log.h>
#include <stdio.h>

//--------------------------------------------
// ffmpeg 读取多媒体信息
//--------------------------------------------

#define PATH "/home/ztiany/code/leaning/1.mp4"

// gcc -g ffmpeg-media-info.c -o media.out `pkg-config --libs libavutil libavformat`
// libavformat` pkg-config --libs libavformat 表示找到 libavformat 并链接此库
int main() {
  int ret = 0;
  AVFormatContext *fmt_context = NULL;

  av_register_all();
  av_log_set_level(AV_LOG_INFO);

  ret = avformat_open_input(&fmt_context, PATH, NULL, NULL);

  if (ret < 0) {
    av_log(NULL, AV_LOG_ERROR, "Can not open file %s\n", av_err2str(ret));
    return -1;
  }

  av_dump_format(fmt_context, 0, PATH, 0);

  avformat_close_input(&fmt_context);

  return 0;
}