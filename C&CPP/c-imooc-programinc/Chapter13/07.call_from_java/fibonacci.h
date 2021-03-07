#ifndef INC_06_CALL_FROM_CPP_07_CALL_FROM_JAVA_FIBONACCI_H_
#define INC_06_CALL_FROM_CPP_07_CALL_FROM_JAVA_FIBONACCI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_bennyhuo_jni_JniMain
 * Method:    factorial
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_bennyhuo_jni_JniMain_fibonacci
    (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif

unsigned int Fibonacci(unsigned int n);

#endif //INC_06_CALL_FROM_CPP_07_CALL_FROM_JAVA_FIBONACCI_H_
