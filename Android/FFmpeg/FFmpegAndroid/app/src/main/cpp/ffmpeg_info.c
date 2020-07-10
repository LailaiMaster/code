#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "Native", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Native", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Native", __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_com_ztiany_ffmpeg_android_FFmpeg_checkFFmepg(JNIEnv *env, jobject thiz) {

    char info[40000] = {0};

    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while (c_temp != NULL) {

        if (c_temp->decode != NULL) {
            sprintf(info, "%s decode:", info);
        } else {
            sprintf(info, "%s encode:", info);
        }

        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s (video):", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s (audio):", info);
                break;
            default:
                sprintf(info, "%s (other):", info);
                break;
        }

        LOGD("%s \n", c_temp->name);

        sprintf(info, "%s[%10s] \n", info, c_temp->name);
        c_temp = c_temp->next;
    }

    return (*env)->NewStringUTF(env, info);
}
