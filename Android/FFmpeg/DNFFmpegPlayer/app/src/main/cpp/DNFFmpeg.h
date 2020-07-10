
#ifndef DNFFMPEGPLAYER_DNFFMPEG_H
#define DNFFMPEGPLAYER_DNFFMPEG_H

#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
}

/*在头文件中进行声明，在 cpp 文件中进行实现。*/
class DNFFmpeg {

public:
    DNFFmpeg(JavaCallHelper *javaCallHelper, const char *dataSource);

    ~DNFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void setRenderFrameCallback(RenderFrameCallback renderFrameCallback);

private:
    char *dataSource = nullptr;
    AVFormatContext *avFormatContext = nullptr;
    JavaCallHelper *javaCallHelper = nullptr;
    AudioChannel *audioChannel = nullptr;
    VideoChannel *videoChannel = nullptr;
    int isPlaying = 0;
    RenderFrameCallback renderFrameCallback;
};

#endif //DNFFMPEGPLAYER_DNFFMPEG_H
