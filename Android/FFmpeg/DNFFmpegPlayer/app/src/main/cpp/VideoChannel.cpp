#include "VideoChannel.h"
#include "macro.h"
#include <queue>

extern "C" {
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
}

void dropAvFrame(queue<AVFrame *> &queue);

void dropAvPacket(queue<AVPacket *> &queue);

VideoChannel::VideoChannel(
        int videoId,
        AVCodecContext *avCodecContext,
        int fps,
        AVRational timeBase
) : fps(fps), BaseChannel(videoId, avCodecContext, timeBase) {
    frames.setSyncHandle(dropAvFrame);
    packets.setSyncHandle(dropAvPacket);
}

VideoChannel::~VideoChannel() {

}

/**
 * 丢包 直到下一个关键帧（这里实现的同步方案为丢帧，这个翰生只是一个参考）
 */
void dropAvPacket(queue<AVPacket *> &q) {
    while (!q.empty()) {
        AVPacket *packet = q.front();
        //如果不属于 I 帧
        if (packet->flags != AV_PKT_FLAG_KEY) {
            BaseChannel::releaseAVPacket(&packet);
            q.pop();
        } else {
            break;
        }
    }
}

/**
 * 丢帧，帧已经解析出来了，不需要关心是什么类型的帧。
 */
void dropAvFrame(queue<AVFrame *> &q) {
    if (!q.empty()) {
        AVFrame *frame = q.front();
        LOGE("视频太慢，丢掉一个帧");
        BaseChannel::releaseAVFrame(&frame);
        q.pop();
    }
}


void *decode_video_task(void *args);

void *render_task(void *args);

/**called by Player, to play the video*/
void VideoChannel::play() {
    LOGD("VideoChannel::play called");
    isPlaying = true;
    //存包的
    packets.setWork(1);
    //存帧的
    frames.setWork(1);
    //开启线程，防止阻塞读流线程。
    //解码
    pthread_create(&pid_decode, nullptr, decode_video_task, this);
    //播放
    pthread_create(&pid_render, nullptr, render_task, this);
    LOGD("VideoChannel::play completed");
}

/**解码任务*/
void *decode_video_task(void *args) {
    auto videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->decodeVideoPacket();
    return nullptr;
}

/**渲染任务*/
void *render_task(void *args) {
    auto videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->renderFrame();
    return nullptr;
}

/**子线程解码 Packet*/
void VideoChannel::decodeVideoPacket() {
    LOGD("VideoChannel start to decode packet");
    AVPacket *avPacket;

    while (isPlaying) {
        //取出一个数据包
        int ret = packets.pop(avPacket);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            //取不到就继续
            continue;
        }
        //FFmeng3.x 后 avcodec_decode_video2 被 avcodec_send_packet 和 avcodec_receive_frame 函数取代。
        //把包丢给解码器
        ret = avcodec_send_packet(avCodecContext, avPacket);
        releaseAVPacket(&avPacket);

        /*AVERROR(EAGAIN): input is not accepted in the current state - user must read output with avcodec_receive_frame()
         * (once all output is read, the packet should be resent, and the call will not fail with EAGAIN).*/
        if (ret == AVERROR(EAGAIN)) {
            //avcodec_send_packet 方法内部的缓冲区已经满了，我们需要使用 avcodec_receive_frame 来读取缓冲区中的数据，以便让其腾出空间。
        } else if (ret < 0/*failed*/) {
            break;
        }

        /*代表一个帧，一个画面*/
        AVFrame *avFrame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, avFrame);
        /*AVERROR(EAGAIN): output is not available in this state - user must try to send new input*/
        if (ret == AVERROR(EAGAIN)) {
            //数据不够，继续send
            //这里的 avFrame 是否应该释放掉？
            continue;
        }
        if (ret != 0) {
            break;
        }
        frames.push(avFrame);
    }//while ending

    //对应 isPlaying 判断时，如果 break，会直接到这里，也需要释放一次。
    releaseAVPacket(&avPacket);
    LOGD("VideoChannel decoding packet ends up");
}

/**子线程：渲染视频*/
void VideoChannel::renderFrame() {
    LOGD("VideoChannel start to render frame");

    //tips：下面两个方法这么多参数不懂怎么办？看 doc 中提供的示例。

    //颜色空间转换：原始数据 YUV420  --> RGBA
    //我们不关心原始数据格式，借助 swsacle 来帮我们做转换
    swsContext = sws_getContext(
            avCodecContext->width, avCodecContext->height, //原始宽高
            avCodecContext->pix_fmt,//格式
            avCodecContext->width, avCodecContext->height,//目标宽高
            AV_PIX_FMT_RGBA,//目标格式
            SWS_BILINEAR,//算法
            nullptr,
            nullptr,
            nullptr
    );

    AVFrame *avFrame = nullptr;
    uint8_t *dst_data[4]; //指针数组
    int dst_linesize[4];//
    //初始化一个图像
    av_image_alloc(dst_data, dst_linesize, avCodecContext->width, avCodecContext->height, AV_PIX_FMT_RGBA, 1);

    //每一帧要展示的时间（单位秒）
    double frameDelays = 1.0 / fps;

    while (isPlaying) {
        int ret = frames.pop(avFrame);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            continue;
        }

        sws_scale(
                swsContext,
                reinterpret_cast<const uint8_t *const *>(avFrame->data),//源数据容器
                avFrame->linesize,//原始行 size，步长，即表示每一行存放的字节长度
                0,//要处理的源图像区域Y轴起始位置，全图则传 0 即可
                avCodecContext->height,//图像的高
                dst_data,//（出参）目标数据容器
                dst_linesize//（出参）目标数据容器每一行存放的字节长度
        );

#if 1//音视频同步逻辑 start
        //获得当前这一个画面播放的相对的时间，视频流使用best_effort_timestamp进行计算
        clock = avFrame->best_effort_timestamp * av_q2d(timeBase);
        //额外的间隔时间（由FFmpeg提供 "extra_delay = repeat_pict / (2*fps)"）
        double extraDelay = avFrame->repeat_pict / (2.0 * fps);
        // 真实需要的间隔时间
        double delays = extraDelay + frameDelays;

        if (audioChannel) {
            if (clock == 0/*第一帧展示时为 0*/) {
                LOGE("音视频同步良好");
                //保证每一帧展示足够的时间。
                av_usleep(delays * 1000000);
            } else {
                double diff = clock - audioChannel->clock;
                //大于0表示视频比较快，小于0则表示音频比较快。
                if (diff > 0) {
                    //大于0 表示视频比较快
                    LOGE("视频快了：%lf", diff);
                    av_usleep((delays + diff) * 1000000);
                } else if(diff<0){
                    //小于0 表示音频比较快
                    LOGE("音频快了：%lf",diff);
                    //视频包积压的太多了 （丢掉一些视频包，不能丢I帧，只能丢B帧和P帧）
                    if (fabs(diff) >= 0.05/*0.05是一个阈值，超过这个值就需要修正*/) {
                        releaseAVFrame(&avFrame);
                        //丢包
                        frames.sync();
                        continue;
                    }else{
                        //不睡了 快点赶上 音频
                    }
                }
            }
        } else {
            av_usleep(delays * 1000000);
        }
#endif//音视频同步逻辑 end

        if (renderFrameCallback) {
            renderFrameCallback(dst_data[0], dst_linesize[0], avCodecContext->width, avCodecContext->height);
        }

        releaseAVFrame(&avFrame);
    }
    av_freep(&dst_data[0]);
    releaseAVFrame(&avFrame);

    LOGD("VideoChannel rendering frame ends up");
}

void VideoChannel::setRenderFrameCallback(RenderFrameCallback renderFrameCallback) {
    this->renderFrameCallback = renderFrameCallback;
}

void VideoChannel::setAudioChannel(AudioChannel *audioChannel) {
    this->audioChannel = audioChannel;
}
