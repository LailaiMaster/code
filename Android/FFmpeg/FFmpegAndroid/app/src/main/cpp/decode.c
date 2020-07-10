#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libswscale/swscale.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "Native", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Native", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Native", __VA_ARGS__)


JNIEXPORT void JNICALL
Java_com_ztiany_ffmpeg_android_FFmpeg_decode2YUV420(JNIEnv *env, jobject thiz, jstring input_jstr,
                                                    jstring output_jstr) {

    const char *input_cstr = (*env)->GetStringUTFChars(env, input_jstr, NULL);
    const char *output_cstr = (*env)->GetStringUTFChars(env, output_jstr, NULL);

    //step 1：   注册组件
    av_register_all();
    //调用avformat_alloc_context，分配一个上下文对象
    //封装格式上下文，统领全局的结构体，保存了视频文件封装格式的相关信息
    AVFormatContext *pFormatCtx = avformat_alloc_context();


    //step 2：打开输入视频文件
    //打开成功后，视频封装信息都保存在pFormatCtx中
    if (avformat_open_input(&pFormatCtx, input_cstr, NULL, NULL) != 0) {
        LOGE("%s", "无法打开输入视频文件");
        goto release;
    }

    //step 3：获取视频文件信息
    //获取成功后，视频文件信息都保存在pFormatCtx中
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE("%s", "无法获取视频文件信息");
        goto release;
    }

    //获取视频流的索引位置：遍历所有类型的流（音频流、视频流、字幕流），找到视频流
    int v_stream_idx = -1;
    int i = 0;
    for (; i < pFormatCtx->nb_streams; i++) {
        //流的类型
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            v_stream_idx = i;
            break;
        }
    }

    if (v_stream_idx == -1) {
        LOGE("%s", "找不到视频流\n");
        goto release;
    }

    //step 4：根据编解码上下文中的编码id查找对应的解码器
    //只有知道视频的编码方式，才能够根据编码方式去找到解码器
    //获取视频流中的编解码上下文
    AVCodecContext *pCodecCtx = pFormatCtx->streams[v_stream_idx]->codec;
    //获取解码器
    AVCodec *pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if (pCodec == NULL) {
        LOGE("%s", "找不到解码器，如果可以就下载解码器，这里直接退出咯\n");
        goto release;
    }

    //step 5：打开解码器
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        LOGE("%s", "解码器无法打开\n");
        return;
    }
    //输出视频信息
    LOGI("视频的文件格式：%s", pFormatCtx->iformat->name);
    LOGI("视频时长：%d", (pFormatCtx->duration) / 1000000);
    LOGI("视频的宽高：%d,%d", pCodecCtx->width, pCodecCtx->height);
    LOGI("解码器的名称：%s", pCodec->name);

    //准备读取
    //AVPacket用于存储一帧一帧的压缩数据（H264）
    //缓冲区，开辟空间
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));

    //AVFrame用于存储解码后的像素数据(YUV)
    //内存分配
    AVFrame *pFrame = av_frame_alloc();
    //YUV420
    AVFrame *pFrameYUV = av_frame_alloc();

    //只有指定了AVFrame的像素格式、画面大小才能真正分配内存
    //缓冲区分配内存
    uint8_t *out_buffer = (uint8_t *) av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
    //初始化缓冲区
    avpicture_fill((AVPicture *) pFrameYUV, out_buffer, AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height);

    //srcW：源图像的宽
    //srcH：源图像的高
    //srcFormat：源图像的像素格式
    //dstW：目标图像的宽
    //dstH：目标图像的高
    //dstFormat：目标图像的像素格式
    //flags：设定图像拉伸使用的算法
    struct SwsContext *sws_ctx = sws_getContext(
            pCodecCtx->width, pCodecCtx->height,
            pCodecCtx->pix_fmt,
            pCodecCtx->width, pCodecCtx->height,
            AV_PIX_FMT_YUV420P,
            SWS_BICUBIC, NULL, NULL, NULL);


    //step 6：一帧一帧的读取压缩数据
    int got_picture, ret;
    FILE *fp_yuv = fopen(output_cstr, "wb+");
    int frame_count = 0;

    //av_read_frame用于读取一帧画面，保存到AVPacket中
    while (av_read_frame(pFormatCtx, packet) >= 0) {
        //只要视频压缩数据（根据流的索引位置判断）
        if (packet->stream_index == v_stream_idx) {

            //step 7：解码一帧视频压缩数据，得到视频像素数据
            //解码后，数据保存到pFrame中
            ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
            if (ret < 0) {
                LOGE("%s", "解码错误");
                return;
            }

            //为0说明解码完成，非0正在解码
            if (got_picture) {
                //AVFrame转为像素格式YUV420
                //AVFrame ---> YUV420P
                //srcSlice[]、dst[]        输入、输出数据
                //srcStride[]、dstStride[] 输入、输出画面一行的数据的大小 AVFrame 转换是一行一行转换的
                //srcSliceY                输入数据第一列要转码的位置 从0开始
                //srcSliceH                输入画面的高度
                sws_scale(sws_ctx,
                          pFrame->data, pFrame->linesize,
                          0, pCodecCtx->height,
                          pFrameYUV->data, pFrameYUV->linesize);

                //输出到YUV文件
                //AVFrame像素帧写入文件
                //data解码后的图像像素数据（音频采样数据）
                //Y 亮度 UV 色度（压缩了） 人对亮度更加敏感
                //U V 个数是Y的1/4
                int y_size = pCodecCtx->width * pCodecCtx->height;
                fwrite(pFrameYUV->data[0], 1, y_size, fp_yuv);
                fwrite(pFrameYUV->data[1], 1, y_size / 4, fp_yuv);
                fwrite(pFrameYUV->data[2], 1, y_size / 4, fp_yuv);

                frame_count++;
                LOGI("解码第%d帧", frame_count);
            }
        }

        //释放资源
        av_free_packet(packet);
    }
    goto release;

    release:
    (*env)->ReleaseStringUTFChars(env, input_jstr, input_cstr);
    (*env)->ReleaseStringUTFChars(env, output_jstr, output_cstr);
}
