#include "jni.h"
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <android/log.h>
#include <android/native_window_jni.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"jason",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"jason",FORMAT,##__VA_ARGS__);

#include "libyuv.h"
#include "queue.h"

//封装格式
#include "libavformat/avformat.h"
//重采样
#include "libswresample/swresample.h"

//nb_streams，视频文件中存在，音频流，视频流，字幕
#define MAX_STREAM 2

#define PACKET_QUEUE_SIZE 50

#define MAX_AUDIO_FRME_SIZE 48000 * 4

typedef struct _Player Player;
typedef struct _DecoderData DecoderData;

static struct _Player {
    JavaVM *javaVM;

    //封装格式上下文
    AVFormatContext *input_format_ctx;
    //音频视频流索引位置
    int video_stream_index;
    int audio_stream_index;
    //流的总个数
    int captrue_streams_no;
    //解码器上下文数组
    AVCodecContext *input_codec_ctx[MAX_STREAM];

    //解码线程ID
    pthread_t decode_threads[MAX_STREAM];
    ANativeWindow *nativeWindow;

    SwrContext *swr_ctx;
    //输入的采样格式
    enum AVSampleFormat in_sample_fmt;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt;
    //输入采样率
    int in_sample_rate;
    //输出采样率
    int out_sample_rate;
    //输出的声道个数
    int out_channel_nb;

    //JNI
    jobject audio_track;
    jmethodID audio_track_write_mid;

    pthread_t thread_read_from_stream;
    //音频，视频队列数组
    Queue *packets[MAX_STREAM];
};

//解码数据
static struct _DecoderData {
    Player *player;
    int stream_index;
};

/**
 * 初始化封装格式上下文，获取音频视频流的索引位置
 */
static void init_input_format_ctx(Player *player, const char *input_cstr) {
    //注册组件
    av_register_all();
    //封装格式上下文
    AVFormatContext *format_ctx = avformat_alloc_context();

    //打开输入视频文件
    if (avformat_open_input(&format_ctx, input_cstr, NULL, NULL) != 0) {
        LOGE("%s", "打开输入视频文件失败");
        return;
    }
    //获取视频信息
    if (avformat_find_stream_info(format_ctx, NULL) < 0) {
        LOGE("%s", "获取视频信息失败");
        return;
    }

    player->captrue_streams_no = format_ctx->nb_streams;
    LOGI("captrue_streams_no:%d", player->captrue_streams_no);
    //视频解码，需要找到视频对应的AVStream所在format_ctx->streams的索引位置
    //获取音频和视频流的索引位置
    int i;
    for (i = 0; i < player->captrue_streams_no; i++) {
        if (format_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            player->video_stream_index = i;
        } else if (format_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            player->audio_stream_index = i;
        }
    }
    player->input_format_ctx = format_ctx;
}

/**
 * 初始化解码器上下文
 */
static void init_codec_context(Player *player, int stream_idx) {
    AVFormatContext *format_ctx = player->input_format_ctx;
    //获取解码器
    LOGI("init_codec_context begin");
    AVCodecContext *codec_ctx = format_ctx->streams[stream_idx]->codec;
    LOGI("init_codec_context end");
    AVCodec *codec = avcodec_find_decoder(codec_ctx->codec_id);
    if (codec == NULL) {
        LOGE("%s", "无法解码");
        return;
    }
    //打开解码器
    if (avcodec_open2(codec_ctx, codec, NULL) < 0) {
        LOGE("%s", "解码器无法打开");
        return;
    }
    player->input_codec_ctx[stream_idx] = codec_ctx;
}

/**
 * 解码视频
 */
static void decode_video(Player *player, AVPacket *packet) {
    //像素数据（解码数据）
    AVFrame *yuv_frame = av_frame_alloc();
    AVFrame *rgb_frame = av_frame_alloc();
    //绘制时的缓冲区
    ANativeWindow_Buffer outBuffer;
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->video_stream_index];
    int got_frame;
    //解码AVPacket->AVFrame
    avcodec_decode_video2(codec_ctx, yuv_frame, &got_frame, packet);
    //Zero if no frame could be decompressed
    //非零，正在解码
    if (got_frame) {
        //lock
        //设置缓冲区的属性（宽、高、像素格式）
        ANativeWindow_setBuffersGeometry(player->nativeWindow, codec_ctx->width, codec_ctx->height,
                                         WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_lock(player->nativeWindow, &outBuffer, NULL);

        //设置rgb_frame的属性（像素格式、宽高）和缓冲区
        //rgb_frame缓冲区与outBuffer.bits是同一块内存
        avpicture_fill((AVPicture *) rgb_frame, outBuffer.bits, AV_PIX_FMT_RGBA, codec_ctx->width,
                       codec_ctx->height);

        //YUV->RGBA_8888
        I420ToARGB(yuv_frame->data[0], yuv_frame->linesize[0],
                   yuv_frame->data[2], yuv_frame->linesize[2],
                   yuv_frame->data[1], yuv_frame->linesize[1],
                   rgb_frame->data[0], rgb_frame->linesize[0],
                   codec_ctx->width, codec_ctx->height);

        //unlock
        ANativeWindow_unlockAndPost(player->nativeWindow);

        usleep(1000 * 16);
    }

    av_frame_free(&yuv_frame);
    av_frame_free(&rgb_frame);
}

/**
 * 音频解码准备
 */
static void decode_audio_prepare(Player *player) {
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->audio_stream_index];

    //重采样设置参数-------------start
    //输入的采样格式
    enum AVSampleFormat in_sample_fmt = codec_ctx->sample_fmt;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16;
    //输入采样率
    int in_sample_rate = codec_ctx->sample_rate;
    //输出采样率
    int out_sample_rate = in_sample_rate;
    //获取输入的声道布局
    //根据声道个数获取默认的声道布局（2个声道，默认立体声stereo）
    //av_get_default_channel_layout(codecCtx->channels);
    uint64_t in_ch_layout = codec_ctx->channel_layout;
    //输出的声道布局（立体声）
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;

    //frame->16bit 44100 PCM 统一音频采样格式与采样率
    SwrContext *swr_ctx = swr_alloc();
    swr_alloc_set_opts(swr_ctx,
                       out_ch_layout, out_sample_fmt, out_sample_rate,
                       in_ch_layout, in_sample_fmt, in_sample_rate,
                       0, NULL);
    swr_init(swr_ctx);

    //输出的声道个数
    int out_channel_nb = av_get_channel_layout_nb_channels(out_ch_layout);

    //重采样设置参数-------------end

    player->in_sample_fmt = in_sample_fmt;
    player->out_sample_fmt = out_sample_fmt;
    player->in_sample_rate = in_sample_rate;
    player->out_sample_rate = out_sample_rate;
    player->out_channel_nb = out_channel_nb;
    player->swr_ctx = swr_ctx;

}

static void jni_audio_prepare(JNIEnv *env, jobject jthiz, Player *player) {
    //JNI begin------------------
    //JasonPlayer
    jclass player_class = (*env)->GetObjectClass(env, jthiz);

    //AudioTrack对象
    jmethodID create_audio_track_mid = (*env)->GetMethodID(env, player_class, "createAudioTrack",
                                                           "(II)Landroid/media/AudioTrack;");
    jobject audio_track = (*env)->CallObjectMethod(env, jthiz, create_audio_track_mid,
                                                   player->out_sample_rate, player->out_channel_nb);

    //调用AudioTrack.play方法
    jclass audio_track_class = (*env)->GetObjectClass(env, audio_track);
    jmethodID audio_track_play_mid = (*env)->GetMethodID(env, audio_track_class, "play", "()V");
    (*env)->CallVoidMethod(env, audio_track, audio_track_play_mid);

    //AudioTrack.write
    jmethodID audio_track_write_mid = (*env)->GetMethodID(env, audio_track_class, "write",
                                                          "([BII)I");

    //JNI end------------------
    player->audio_track = (*env)->NewGlobalRef(env, audio_track);
    //(*env)->DeleteGlobalRef
    player->audio_track_write_mid = audio_track_write_mid;
}

/**
 * 音频解码
 */
static void decode_audio(Player *player, AVPacket *packet) {
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->audio_stream_index];
    LOGI("%s", "decode_audio");
    //解压缩数据
    AVFrame *frame = av_frame_alloc();
    int got_frame;
    avcodec_decode_audio4(codec_ctx, frame, &got_frame, packet);

    //16bit 44100 PCM 数据（重采样缓冲区）
    uint8_t *out_buffer = (uint8_t *) av_malloc(MAX_AUDIO_FRME_SIZE);
    //解码一帧成功
    if (got_frame > 0) {
        swr_convert(player->swr_ctx, &out_buffer, MAX_AUDIO_FRME_SIZE,
                    (const uint8_t **) frame->data, frame->nb_samples);
        //获取sample的size
        int out_buffer_size = av_samples_get_buffer_size(NULL, player->out_channel_nb,
                                                         frame->nb_samples, player->out_sample_fmt,
                                                         1);

        //关联当前线程的JNIEnv
        JavaVM *javaVM = player->javaVM;
        JNIEnv *env;
        (*javaVM)->AttachCurrentThread(javaVM, &env, NULL);

        //out_buffer缓冲区数据，转成byte数组
        jbyteArray audio_sample_array = (*env)->NewByteArray(env, out_buffer_size);
        jbyte *sample_bytep = (*env)->GetByteArrayElements(env, audio_sample_array, NULL);
        //out_buffer的数据复制到sampe_bytep
        memcpy(sample_bytep, out_buffer, out_buffer_size);
        //同步
        (*env)->ReleaseByteArrayElements(env, audio_sample_array, sample_bytep, 0);

        //AudioTrack.write PCM数据
        (*env)->CallIntMethod(env, player->audio_track, player->audio_track_write_mid,
                              audio_sample_array, 0, out_buffer_size);
        //释放局部引用
        (*env)->DeleteLocalRef(env, audio_sample_array);

        (*javaVM)->DetachCurrentThread(javaVM);

        usleep(1000 * 16);
    }

    av_frame_free(&frame);
}


/**
 * 解码子线程函数（消费）
 */
static void *decode_data(void *arg) {
    DecoderData *decoder_data = (DecoderData *) arg;
    Player *player = decoder_data->player;
    int stream_index = decoder_data->stream_index;
    //根据stream_index获取对应的AVPacket队列
    Queue *queue = player->packets[stream_index];

    AVFormatContext *format_ctx = player->input_format_ctx;
    //编码数据

    //6.一阵一阵读取压缩的视频数据AVPacket
    int video_frame_count = 0, audio_frame_count = 0;
    for (;;) {
        //消费AVPacket
        AVPacket *packet = (AVPacket *) queue_pop(queue);
        if (stream_index == player->video_stream_index) {
            decode_video(player, packet);
            LOGI("video_frame_count:%d", video_frame_count++);
        } else if (stream_index == player->audio_stream_index) {
            decode_audio(player, packet);
            LOGI("audio_frame_count:%d", audio_frame_count++);
        }

    }
}

static void decode_video_prepare(JNIEnv *env, Player *player, jobject surface) {
    player->nativeWindow = ANativeWindow_fromSurface(env, surface);
}

/**
 * 给AVPacket开辟空间，后面会将AVPacket栈内存数据拷贝至这里开辟的空间
 */
static void *player_fill_packet() {
    //请参照我在vs中写的代码
    AVPacket *packet = malloc(sizeof(AVPacket));
    return packet;
}

/**
 * 初始化音频，视频AVPacket队列，长度50
 */
static void player_alloc_queues(Player *player) {
    int i;
    //这里，正常是初始化两个队列
    for (i = 0; i < player->captrue_streams_no; ++i) {
        Queue *queue = queue_init(PACKET_QUEUE_SIZE, (queue_fill_func) player_fill_packet);
        player->packets[i] = queue;
        //打印视频音频队列地址
        LOGI("stream index:%d,queue:%#x", i, queue);
    }
}

static void *packet_free_func(AVPacket *packet) {
    av_free_packet(packet);
    return 0;
}

/**
 * 生产者线程：负责不断的读取视频文件中AVPacket，分别放入两个队列中
 */
static void *player_read_from_stream(Player *player) {
    int index = 0;
    int ret;
    //栈内存上保存一个AVPacket
    AVPacket packet, *pkt = &packet;
    for (;;) {
        ret = av_read_frame(player->input_format_ctx, pkt);
        //到文件结尾了
        if (ret < 0) {
            break;
        }
        //根据AVpacket->stream_index获取对应的队列
        Queue *queue = player->packets[pkt->stream_index];

        //示范队列内存释放
        //queue_free(queue,packet_free_func);

        //将AVPacket压入队列
        AVPacket *packet_data = queue_push(queue);
        //拷贝（间接赋值，拷贝结构体数据）
        *packet_data = packet;
        LOGI("queue:%#x, packet:%#x", queue, packet);
    }
}


JNIEXPORT jstring JNICALL Java_com_ztiany_ffmpeg_android_FFmpeg_play
        (JNIEnv *env, jobject jobj, jstring input_jstr, jobject surface) {

    const char *input_cstr = (*env)->GetStringUTFChars(env, input_jstr, NULL);
    Player *player = (Player *) malloc(sizeof(Player));
    (*env)->GetJavaVM(env, &(player->javaVM));

    //初始化封装格式上下文
    init_input_format_ctx(player, input_cstr);
    int video_stream_index = player->video_stream_index;
    int audio_stream_index = player->audio_stream_index;
    //获取音视频解码器，并打开
    init_codec_context(player, video_stream_index);
    init_codec_context(player, audio_stream_index);


    decode_video_prepare(env, player, surface);
    decode_audio_prepare(player);

    jni_audio_prepare(env, jobj, player);

    player_alloc_queues(player);

    //生产者线程
    pthread_create(&(player->thread_read_from_stream), NULL, player_read_from_stream,
                   (void *) player);
    sleep(5);

    //消费者线程
    DecoderData data1 = {player, video_stream_index}, *decoder_data1 = &data1;
    pthread_create(&(player->decode_threads[video_stream_index]), NULL, decode_data,
                   (void *) decoder_data1);

    DecoderData data2 = {player, audio_stream_index}, *decoder_data2 = &data2;
    pthread_create(&(player->decode_threads[audio_stream_index]), NULL, decode_data,
                   (void *) decoder_data2);


    pthread_join(player->thread_read_from_stream, NULL);
    pthread_join(player->decode_threads[video_stream_index], NULL);
    pthread_join(player->decode_threads[audio_stream_index], NULL);

    /*ANativeWindow_release(nativeWindow);
    av_frame_free(&yuv_frame);
    avcodec_close(pCodeCtx);
    avformat_free_context(pFormatCtx);

    (*env)->ReleaseStringUTFChars(env,input_jstr,input_cstr);

    free(player);*/

}

