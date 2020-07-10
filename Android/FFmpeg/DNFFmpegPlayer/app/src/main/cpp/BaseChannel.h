#ifndef DNFFMPEGPLAYER_BASECHANNEL_H
#define DNFFMPEGPLAYER_BASECHANNEL_H

#import "safe_queue.h"

extern "C" {
#include <libavcodec/avcodec.h>
};

class BaseChannel {

public:
    BaseChannel(int id, AVCodecContext *codecContext, AVRational time_base)
            : id(id), avCodecContext(codecContext), timeBase(time_base) {

        packets.setReleaseCallback(releaseAVPacket);
        frames.setReleaseCallback(releaseAVFrame);

    }

    virtual ~BaseChannel() {
        packets.clear();
        frames.clear();
    }

    /**Stream Id*/
    int id;

    AVCodecContext *avCodecContext = nullptr;

    /**待解码数据包队列*/
    SafeQueue<AVPacket *> packets;

    /**解码后的数据包队列*/
    SafeQueue<AVFrame *> frames;

    /**用于音视频同步*/
    AVRational timeBase;
    double clock;

    bool isPlaying = false;

    virtual void play() = 0;

    static void releaseAVPacket(AVPacket **avPacket) {
        if (avPacket) {
            av_packet_free(avPacket);
            *avPacket = nullptr;
        }
    }

    static void releaseAVFrame(AVFrame **avFrame) {
        if (avFrame) {
            av_frame_free(avFrame);
            *avFrame = nullptr;
        }
    }
};

#endif