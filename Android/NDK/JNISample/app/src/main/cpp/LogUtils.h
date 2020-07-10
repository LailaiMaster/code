#ifndef JNISAMPLE_LOGUTILS_H
#define JNISAMPLE_LOGUTILS_H

#define LOG_TAG "C-Log"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#endif //JNISAMPLE_LOGUTILS_H
