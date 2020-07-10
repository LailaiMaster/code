#include <jni.h>
#include <string>
#include "DNFFmpeg.h"
#include "macro.h"
/**native window, used to process window at native layer. native_window requires a android lib android.so*/
#include <android/native_window_jni.h>

void renderFrame(uint8_t *srcData, int srcLineSize, int width, int height);

JavaVM *globalJvm = nullptr;
DNFFmpeg *dnfFmpeg = nullptr;
ANativeWindow *nativeWindow = nullptr;
pthread_mutex_t windowMutex = PTHREAD_MUTEX_INITIALIZER;

extern "C"
JNIEXPORT void JNICALL
Java_com_dongnao_player_DNPlayer_nativePrepare(JNIEnv *env, jobject thiz, jstring dataSource) {
    //创建FFmpeg
    const char *string = env->GetStringUTFChars(dataSource, nullptr);
    auto *javaCallHelper = new JavaCallHelper(globalJvm, env, thiz);
    dnfFmpeg = new DNFFmpeg(javaCallHelper, string);
    dnfFmpeg->setRenderFrameCallback(renderFrame);
    LOGD("data-source = %s", string);
    //调用 native 层的 prepare
    dnfFmpeg->prepare();
    env->ReleaseStringUTFChars(dataSource, string);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    globalJvm = jvm;
    LOGD("JNI_OnLoad");
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dongnao_player_DNPlayer_nativeDestroy(JNIEnv *env, jobject thiz) {
    dnfFmpeg = nullptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dongnao_player_DNPlayer_nativeStart(JNIEnv *env, jobject thiz) {
    if (!dnfFmpeg) {
        return;
    }
    dnfFmpeg->start();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dongnao_player_DNPlayer_nativeSetSurface(JNIEnv *env, jobject thiz, jobject surface) {
    LOGD("nativeSetSurface called");
    pthread_mutex_lock(&windowMutex);

    if (nativeWindow) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = nullptr;
    }
    nativeWindow = ANativeWindow_fromSurface(env, surface);

    pthread_mutex_unlock(&windowMutex);
    LOGD("nativeSetSurface finished");
}

void renderFrame(uint8_t *srcData, int srcLineSize, int width, int height) {

    pthread_mutex_lock(&windowMutex);

    if (!nativeWindow) {
        pthread_mutex_unlock(&windowMutex);
        return;
    }

    //设置窗口属性
    ANativeWindow_setBuffersGeometry(nativeWindow, width, height, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer nativeWindowBuffer{};
    if (ANativeWindow_lock(nativeWindow, &nativeWindowBuffer, 0)) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = nullptr;
        return;
    }
    //填充rgb数据给dst_data
    uint8_t *dst_data = static_cast<uint8_t *>(nativeWindowBuffer.bits);
    //拷贝要渲染的数据
    //stride：一行多少个数据，有 RGBA 四种色，则 *4
    //由此推断出：一行数据包含 RGBA
    int dstLineSize = nativeWindowBuffer.stride * 4;
    //一行一行地拷贝
    for (int i = 0; i < nativeWindowBuffer.height; ++i) {
        memcpy(dst_data + i * dstLineSize, srcData + i * srcLineSize, dstLineSize);
    }
    ANativeWindow_unlockAndPost(nativeWindow);

    pthread_mutex_unlock(&windowMutex);
}