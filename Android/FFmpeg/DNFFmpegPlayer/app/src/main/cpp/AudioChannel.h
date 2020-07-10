#ifndef DNFFMPEGPLAYER_AUDIOCHANNEL_H
#define DNFFMPEGPLAYER_AUDIOCHANNEL_H

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "BaseChannel.h"

extern "C" {
#include <libswresample/swresample.h>
};

class AudioChannel : public BaseChannel {
public:
    AudioChannel(int audioId, AVCodecContext *avCodecContext, AVRational timeBase);

    ~AudioChannel();

    void play();

    void decodeAudioPacket();

    void playAudio();

    int getPcm();

public:
    uint8_t *data = 0;
    /**声道数*/
    int out_channels;
    /**采样位(字节为单位)，比如16位就是两个字节*/
    int out_samplesize;
    /**采样率*/
    int out_sample_rate;

private:
    pthread_t pid_decode = 0;
    pthread_t pid_sound = 0;

    /**
     * OpenSL ES
     */
    // 引擎与引擎接口
    SLObjectItf engineObject = 0;
    SLEngineItf engineInterface = 0;
    //混音器
    SLObjectItf outputMixObject = 0;
    //播放器
    SLObjectItf bqPlayerObject = 0;
    //播放器接口
    SLPlayItf bqPlayerInterface = 0;

    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueueInterface = 0;

    /**FFmpeg 重采样*/
    SwrContext *swrContext = nullptr;
};

#endif //DNFFMPEGPLAYER_AUDIOCHANNEL_H
