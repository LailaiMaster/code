#ifndef DNFFMPEGPLAYER_JAVACALLHELPER_H
#define DNFFMPEGPLAYER_JAVACALLHELPER_H

#include <jni.h>

class JavaCallHelper {

public:
    JavaCallHelper(JavaVM *vm, JNIEnv *jniEnv, jobject instance);

    ~JavaCallHelper();

    /**
     * thread 标识是否为主线程，参考 macro.h 中的定义。
     * code 错误码
     */
    void onError(int thread, int code);

    void onPrepare(int i);

private:
    JavaVM *javaVm;
    JNIEnv *jniEnv;
    jobject javaInterface;
    jmethodID onErrorId;
    jmethodID onPreparedId;
};

#endif