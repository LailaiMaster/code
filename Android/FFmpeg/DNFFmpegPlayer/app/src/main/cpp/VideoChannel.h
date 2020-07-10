#ifndef DNFFMPEGPLAYER_VIDEOCHANNEL_H
#define DNFFMPEGPLAYER_VIDEOCHANNEL_H

#include "BaseChannel.h"
#include "AudioChannel.h"

extern "C" {
#include <libswscale/swscale.h>
}

/**
 * frame 渲染回调
 *
 * 参数1：图像数据
 * 参数2：每一个行的字节数
 * 参数3：图像宽
 * 参数4：图像高
 */
typedef void (*RenderFrameCallback)(uint8_t *, int, int, int);

class VideoChannel : public BaseChannel {

public:
    VideoChannel(int videoId, AVCodecContext *avCodecContext, int fps, AVRational timeBase);

    ~VideoChannel();

    /**开始解码+播放*/
    void play();

    /**解码*/
    void decodeVideoPacket();

    /**播放*/
    void renderFrame();

    /**设置回调，用于获取解码后的数据*/
    void setRenderFrameCallback(RenderFrameCallback renderFrameCallback);

    void setAudioChannel(AudioChannel *audioChannel);

private:
    pthread_t pid_decode = 0;
    pthread_t pid_render = 0;
    RenderFrameCallback renderFrameCallback = nullptr;
    SwsContext *swsContext = nullptr;
    int fps;
    AudioChannel *audioChannel = nullptr;
};

#endif
