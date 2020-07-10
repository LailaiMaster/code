#ifndef JNISAMPLE_UTILS_H
#define JNISAMPLE_UTILS_H

#include "jni.h"

char *jString2CString(JNIEnv *env, jstring jstr);

void cStringToJString(JNIEnv *env, char *str, jobject *receive);

#endif
