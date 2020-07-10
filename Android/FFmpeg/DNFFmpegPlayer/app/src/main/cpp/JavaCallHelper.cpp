#include "JavaCallHelper.h"
#include "macro.h"

JavaCallHelper::JavaCallHelper(JavaVM *vm, JNIEnv *jniEnv, jobject instance) {
    this->javaVm = vm;
    //在主线程，则直接使用该 env。
    this->jniEnv = jniEnv;
    //一旦涉及到 jobject 跨线程调用，就需要建立 GlobalRef
    this->javaInterface = jniEnv->NewGlobalRef(instance);
    jclass instanceClass = jniEnv->GetObjectClass(instance);
    //获取错误回调方法（为什么这里不需要 NewGlobalRef ？）
    onErrorId = jniEnv->GetMethodID(instanceClass, "onNativeError", "(I)V");
    onPreparedId = jniEnv->GetMethodID(instanceClass, "onNativePrepared", "()V");
}

JavaCallHelper::~JavaCallHelper() {
    jniEnv->DeleteGlobalRef(javaInterface);
}

void JavaCallHelper::onError(int thread, int code) {
    if (thread == THREAD_MAIN) {
        jniEnv->CallVoidMethod(javaInterface, onErrorId, code);
    } else {
        JNIEnv *subEnv;
        javaVm->AttachCurrentThread(&subEnv, nullptr);
        subEnv->CallVoidMethod(javaInterface, onErrorId, code);
        javaVm->DetachCurrentThread();
    }
}

void JavaCallHelper::onPrepare(int thread) {
    if (thread == THREAD_MAIN) {
        jniEnv->CallVoidMethod(javaInterface, onPreparedId);
    } else {
        JNIEnv *subEnv;
        javaVm->AttachCurrentThread(&subEnv, nullptr);
        subEnv->CallVoidMethod(javaInterface, onPreparedId);
        javaVm->DetachCurrentThread();
    }
}
