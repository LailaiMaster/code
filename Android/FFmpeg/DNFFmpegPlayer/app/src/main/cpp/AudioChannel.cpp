#include "AudioChannel.h"
#include "macro.h"

AudioChannel::AudioChannel(
        int audioId,
        AVCodecContext *avCodecContext,
        AVRational timeBase
) : BaseChannel(audioId, avCodecContext, timeBase) {
    /*下面初始化冲采用需要用到的数据*/
    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    out_samplesize = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    out_sample_rate = 44100;
    //44100 个 16 位 44100 * 2
    //44100 *(双声道) * (16位)
    data = static_cast<uint8_t *>(malloc(out_sample_rate * out_channels * out_samplesize));
    memset(data, 0, out_sample_rate * out_channels * out_samplesize);
}

AudioChannel::~AudioChannel() {
    if (data) {
        free(data);
        data = 0;
    }
}

void *decode_audio_task(void *args);

void *play_audio_task(void *args);

void AudioChannel::play() {
    LOGD("AudioChannel::play called");
    isPlaying = true;
    //存包的
    packets.setWork(1);
    //存帧的
    frames.setWork(1);

    //初始化 FFmpeg 重采样上下文
    //0+输出声道+输出采样位+输出采样率+  输入的3个参数
    swrContext = swr_alloc_set_opts(
            nullptr,//existing Swr context if available, or NULL if not

            AV_CH_LAYOUT_STEREO,//目标通过：左右双声道
            AV_SAMPLE_FMT_S16,//目标采样位：16 位
            out_sample_rate,//目标采样率：44100

            avCodecContext->channel_layout,//原声音通道
            avCodecContext->sample_fmt,//源声音采样格式
            avCodecContext->sample_rate,//源声音采样率
            0,//日志参数
            nullptr//日志参数
    );
    //初始化
    swr_init(swrContext);

    //开始线程解码音频
    pthread_create(&pid_decode, nullptr, decode_audio_task, this);
    //开始线程播放音频
    pthread_create(&pid_sound, nullptr, play_audio_task, this);
}

void *decode_audio_task(void *args) {
    auto audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->decodeAudioPacket();
    return nullptr;
}

void *play_audio_task(void *args) {
    auto audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->playAudio();
    return nullptr;
}

void AudioChannel::decodeAudioPacket() {
    LOGD("AudioChannel start to decode packet");
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
    LOGD("AudioChannel decoding packet ends up");
}

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context);

/*
 * 很复杂，参考：
 * https://github.com/android/ndk-samples/blob/master/native-audio/app/src/main/cpp/native-audio-jni.c
 * https://www.jianshu.com/p/e94652ee371c
 */
void AudioChannel::playAudio() {
    LOGD("playAudio start to play audio");

    /*
     * 1、创建引擎与接口
     */
    SLresult result;
    //1.1 创建引擎 SLObjectItf engineObject
    result = slCreateEngine(&engineObject, 0, nullptr, 0, nullptr, nullptr);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    //1.2 初始化引擎
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    //1.3 获取引擎接口SLEngineItf engineInterface
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }

    /*
     * 2 设置混音器：用于实现声音效果，比如礼堂效果...
     */
    //2.1 创建混音器 SLObjectItf outputMixObject
    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0, nullptr, nullptr);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    //2.2 初始化混音器 outputMixObject
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }

    /*
    * 3 配置输入声音信息
    */
    //3.1 创建buffer缓冲类型的队列 2个队列
    SLDataLocator_AndroidSimpleBufferQueue android_queue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
            2};
    //pcm数据格式：pcm+2(双声道)+44100(采样率)+ 16(采样位)+16(数据的大小)+LEFT|RIGHT(双声道)+小端数据
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,//格式必须为 SL_DATAFORMAT_PCM
            2,//声道数
            SL_SAMPLINGRATE_44_1,//采样率，表示 1s 采集多少次声音
            SL_PCMSAMPLEFORMAT_FIXED_16,//采样位，这里16bit 2字节
            SL_PCMSAMPLEFORMAT_FIXED_16,//数据大小
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//左右双声道
            SL_BYTEORDER_LITTLEENDIAN//小端数据（低字节在前）
    };
    //数据源：将上述配置信息放到这个数据源中（输入的声音有不同的格式，这里我们设置需要播放声音的格式。）
    SLDataSource slDataSource = {&android_queue, &pcm};
    //3.2 配置音轨(输出)
    //设置混音器
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, nullptr};
    //需要的接口
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    //3.3 创建播放器
    (*engineInterface)->CreateAudioPlayer(
            engineInterface,
            &bqPlayerObject,
            &slDataSource,
            &audioSnk,
            1,
            ids,
            req);
    //初始化播放器
    (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
    //得到接口后调用  获取Player接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);

    /*
    * 4 设置播放回调函数
    */
    //获取播放器队列接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueueInterface);
    //设置回调
    (*bqPlayerBufferQueueInterface)->RegisterCallback(bqPlayerBufferQueueInterface, bqPlayerCallback, this);

    /*
     * 5 设置播放状态
     */
    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);

    /*
     * 6、手动激活一下这个回调
     */
    bqPlayerCallback(bqPlayerBufferQueueInterface, this);
    LOGD("playAudio play playing audio ends up");
}

/**返回获取的pcm数据的字节数。*/
int AudioChannel::getPcm() {
    int dataSize = 0;

    AVFrame *avFrame = av_frame_alloc();
    int ret = frames.pop(avFrame);
    if (!isPlaying) {
        if (ret) {
            releaseAVFrame(&avFrame);
        }
        return dataSize;
    }

    //OpenSL ES 的初始化中我们指定了要播放的声音源的格式，而这里实际的音频格式或采样率可能与设置的不一样，所以需要在这里进行重采样。
    //比如： 48000HZ 8位 =》 44100 16位，下面进行冲采用：

    //假设我们输入了 10 个数据 ，而 swrContext 转码器这一次处理了8个数据，剩余的两个就会先保存到 swr_convert 函数内部容器中。
    // 那么如果不加 delays(上次没处理完的数据) , 没处理完的数据就会一直积压。
    int64_t delays = swr_get_delay(swrContext, avFrame->sample_rate);

    // 计算将 nb_samples 个数据由 sample_rate 采样率转成 44100 后，返回多少个数据。（这里将 44100、48000看成单位即可）
    int64_t max_samples = av_rescale_rnd(
            delays + avFrame->nb_samples,//上次积压的没有完的数据量 + 上现有的数据量
            out_sample_rate,//目标采样率
            avFrame->sample_rate,//原始采样率
            AV_ROUND_UP// AV_ROUND_UP : 向上取整 1.1 = 2
    );
    //上下文+输出缓冲区+输出缓冲区能接受的最大数据量+输入数据+输入数据个数
    //返回每一个声道的采样数（输出数据）
    int samples = swr_convert(
            swrContext,
            &data,
            max_samples,
            (const uint8_t **) avFrame->data,
            avFrame->nb_samples
    );
    //samples 个   * 2 声道 * 2字节（16位） = 总的数据量大小
    dataSize = samples * out_samplesize * out_channels;

    //（用于时间同步，提供给 VideoChannel 做时间同步，音频流使用 fps 进行计算）获取 frame 的一个相对播放时间 （相对于开始播放的时间），获得相对播放这一段数据的秒数。
    clock = avFrame->pts * av_q2d(timeBase);

    return dataSize;
}

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    auto *audioChannel = static_cast<AudioChannel *>(context);
    //获得 pcm 数据，多少个字节 data。
    int dataSize = audioChannel->getPcm();
    if (dataSize > 0) {
        // 接收 16 位数据，入队则自动播放。
        (*bq)->Enqueue(bq, audioChannel->data, dataSize);
    }
}