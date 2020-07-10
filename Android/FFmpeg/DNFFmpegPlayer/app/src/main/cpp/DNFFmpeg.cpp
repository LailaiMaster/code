#include "DNFFmpeg.h"
#include <cstring>
#include <pthread.h>
#include "macro.h"

void *task_prepare(void *);

void *task_play(void *);

DNFFmpeg::DNFFmpeg(JavaCallHelper *javaCallHelper, const char *dataSource) {
    //防止 dataSource 指向的内存被释放。
    this->dataSource = new char[strlen(dataSource) + 1];//strlen 不读取字符串结束符 '0'
    this->javaCallHelper = javaCallHelper;
    //拷贝数据
    strcpy(this->dataSource, dataSource);
}

DNFFmpeg::~DNFFmpeg() {
    DELETE(dataSource)
    DELETE(javaCallHelper)
}

void DNFFmpeg::prepare() {
    //创建线程
    pthread_t tid;
    pthread_create(&tid, nullptr, task_prepare, this);
}

void *task_prepare(void *args) {
    auto *dnfFmpeg = static_cast<DNFFmpeg *>(args);
    dnfFmpeg->_prepare();
    /*线程函数一定要 return*/
    return nullptr;
}

void DNFFmpeg::_prepare() {
    LOGD("_prepare");
    //初始化网络，否则 FFmpeg 无法联网
    avformat_network_init();
    /*
     * step1：打开媒体地址
     *
     * 参数说明：
     *
     *  AVFormatContext** ps：封装格式上下文结构体，全局结构体，保存了视频文件封装格式相关信息。一般让我们传递二级指针，意味着被调用函数回去修改该指针的指向，不需要我们创建对象。
     *  char* url：媒体文件地址，可以是本地文件，也可以是网络地址。
     *
     *  返回值：/0 on success
     */
    avFormatContext = nullptr;
    int result = avformat_open_input(&avFormatContext, this->dataSource, nullptr, nullptr);
    LOGD("avformat_open_input result = %d,", result);

    if (result != 0/*success*/ || avFormatContext == nullptr) {
        LOGE("step1：打开媒体地址 失败");
        javaCallHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        return;
    }

    //step2：查找媒体中的音视频流，返回值：>=0 if OK
    result = avformat_find_stream_info(avFormatContext, nullptr);

    if (result < 0) {
        LOGE("step2：查找媒体中的音视频流 失败");
        javaCallHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        return;
    }

    //step2 成功后，nb_streams 就有值了，nb_streams 表示媒体流的个数，然后开始处理音视频流
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        //获取一个媒体流，可能是视频也可能是音频
        AVStream *stream = avFormatContext->streams[i];
        //包含解码这段流的各种参数信息，比如  avCodecParameters->bit_rate 表示比特率（码流，越大越清晰）
        AVCodecParameters *avCodecParameters = stream->codecpar;

        //下面对媒体流进行处理：
        /*
         * 对所有媒体流都需要做的操作：
         *      1. 通过当前流使用的解码方式，查找解码器
         *      2. 获得解码器上下文
         *      3. 设置上下文的一些参数
         *      4. 打开编码器
         */
        //通过当前流使用的解码方式，查找解码器
        AVCodec *avCodec = avcodec_find_decoder(avCodecParameters->codec_id);
        if (!avCodec/*如果不支持这种解码方式，就等于 null 了*/) {
            LOGE("step2：avcodec_find_decoder 失败");
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            return;
        }
        //获得解码器上下文
        AVCodecContext *avCodecContext = avcodec_alloc_context3(avCodec);
        if (!avCodecContext) {
            LOGE("step2：avcodec_alloc_context3 失败");
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            return;
        }
        //设置上下文的一些参数（调用这个方法就是自带设置视频参数），返回值 >= 0 on success
        result = avcodec_parameters_to_context(avCodecContext, avCodecParameters);
        if (result < 0) {
            LOGE("step2：avcodec_parameters_to_context 失败");
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            return;
        }
        result = avcodec_open2(avCodecContext, avCodec, nullptr);//zero on success
        if (result != 0) {
            LOGE("step2：avcodec_open2 失败");
            javaCallHelper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            return;
        }

        //PTS 单位
        AVRational &timeBase = stream->time_base;

        //针对音频和视频做不同的处理
        if (avCodecParameters->codec_type == AVMEDIA_TYPE_AUDIO/*音频*/) {
            audioChannel = new AudioChannel(i, avCodecContext, timeBase);
        } else if (avCodecParameters->codec_type == AVMEDIA_TYPE_VIDEO/*视频，一般字幕都和视频和到一起了*/) {
            //获取帧率，确定每一帧要展示多久。
            AVRational avRational = stream->avg_frame_rate;
            int fps = (int) av_q2d(avRational);
            //视频流处理通道
            videoChannel = new VideoChannel(i, avCodecContext, fps, timeBase);
        }
    }

    //没有音视频数据
    if (!audioChannel && !videoChannel) {
        LOGE("step2：没有找到音视频数据流 失败");
        javaCallHelper->onError(THREAD_CHILD, FFMPEG_NOMEDIA);
    } else {
        LOGD("step2：查找媒体中的音视频流 完成");
        //初始化完毕
        javaCallHelper->onPrepare(THREAD_CHILD);
    }

}

void DNFFmpeg::start() {
    //标识位，开始工作
    isPlaying = 1;
    //准备好音频频道
    if (audioChannel) {
        audioChannel->play();
    }
    //准备好视频频道
    if (videoChannel) {
        videoChannel->setAudioChannel(audioChannel);
        videoChannel->setRenderFrameCallback(renderFrameCallback);
        videoChannel->play();
    }
    //创建线程
    pthread_t tid;
    pthread_create(&tid, nullptr, task_play, this);
}

void *task_play(void *args) {
    auto dnPlayer = static_cast<DNFFmpeg *>(args);
    dnPlayer->_start();
    /*线程函数一定要 return*/
    return nullptr;
}

void DNFFmpeg::_start() {
    //读取音视频数据包
    while (isPlaying) {
        AVPacket *avPacket = av_packet_alloc();
        int result = av_read_frame(avFormatContext, avPacket);//0 if OK, < 0 on error or end of file
        if (result == 0) {
            //stream_index 对应在 stream[] 中元素的索引。
            if (audioChannel && avPacket->stream_index == audioChannel->id) {
                audioChannel->packets.push(avPacket);
            } else if (videoChannel && avPacket->stream_index == videoChannel->id) {
                videoChannel->packets.push(avPacket);
            }
        } else if (result == AVERROR_EOF) { /*解码与播放是异步进行的，读取完成，不代表播放完成*/

        } else {/*error*/

        }
    }
}

void DNFFmpeg::setRenderFrameCallback(RenderFrameCallback renderFrameCallback) {
    this->renderFrameCallback = renderFrameCallback;
}
