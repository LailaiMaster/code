#include <jni.h>
#include <string>

#include <android/log.h>
#include <android/bitmap.h>
#include <android/window.h>
#include <android/native_window.h>

#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#define LOG_TAG "native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define DEFAULT_CARD_WIDTH 640
#define DEFAULT_CARD_HEIGHT 400
#define FIX_IDCARD_SIZE Size(DEFAULT_CARD_WIDTH,DEFAULT_CARD_HEIGHT)

using namespace cv;
using namespace std;

void
nBitmapToMat2(JNIEnv *env, jclass, jobject bitmap, jlong m_addr, jboolean needUnPremultiplyAlpha) {

    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &dst = *((Mat *) m_addr);

    try {
        LOGD("nBitmapToMat");
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        dst.create(info.height, info.width, CV_8UC4);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            LOGD("nBitmapToMat: RGBA_8888 -> CV_8UC4");
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (needUnPremultiplyAlpha) cvtColor(tmp, dst, COLOR_mRGBA2RGBA);
            else tmp.copyTo(dst);
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            LOGD("nBitmapToMat: RGB_565 -> CV_8UC4");
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            cvtColor(tmp, dst, COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nBitmapToMat caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if (!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nBitmapToMat caught unknown exception (...)");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}

void
nMatToBitmap2(JNIEnv *env, jclass, jlong m_addr, jobject bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &src = *((Mat *) m_addr);

    try {
        LOGD("nMatToBitmap");
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(src.dims == 2 && info.height == (uint32_t) src.rows &&
                  info.width == (uint32_t) src.cols);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (src.type() == CV_8UC1) {
                LOGD("nMatToBitmap: CV_8UC1 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if (src.type() == CV_8UC3) {
                LOGD("nMatToBitmap: CV_8UC3 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_RGB2RGBA);
            } else if (src.type() == CV_8UC4) {
                LOGD("nMatToBitmap: CV_8UC4 -> RGBA_8888");
                if (needPremultiplyAlpha) cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if (src.type() == CV_8UC1) {
                LOGD("nMatToBitmap: CV_8UC1 -> RGB_565");
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if (src.type() == CV_8UC3) {
                LOGD("nMatToBitmap: CV_8UC3 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if (src.type() == CV_8UC4) {
                LOGD("nMatToBitmap: CV_8UC4 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap caught cv::Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if (!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap caught unknown exception (...)");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

jobject createBitmap(JNIEnv *env, Mat srcData, jobject config) {
    int imgWidth = srcData.cols;
    int imgHeight = srcData.rows;
    jclass bmpCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMid = env->GetStaticMethodID(bmpCls, "createBitmap",
                                                       "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject jBmpObj = env->CallStaticObjectMethod(bmpCls, createBitmapMid, imgWidth, imgHeight,
                                                  config);
    nMatToBitmap2(env, nullptr, (jlong) &srcData, jBmpObj, static_cast<jboolean>(false));
    return jBmpObj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_me_ztiany_opencv_idrecognizing_ImageProcess_getIdNumber(
        JNIEnv *env,
        jclass clazz,
        jobject src,
        jobject config
) {

    LOGI("getIdNumber");

    Mat src_img;
    Mat dest_img;
    Mat number_img;
    bool found = false;

    nBitmapToMat2(env, clazz, src, (jlong) &src_img, 0);

    //变为 640*400 的size，这是身份证的size。
    resize(src_img, src_img, FIX_IDCARD_SIZE);
    //灰度化
    cvtColor(src_img, dest_img, COLOR_BGR2GRAY);
    //二值化
    threshold(dest_img, dest_img, 100, 255, THRESH_BINARY);
    //图形膨胀
    Mat erodeElement = getStructuringElement(MORPH_RECT, Size(20, 10));
    erode(dest_img, dest_img, erodeElement);

    //轮廓检测
    vector<vector<Point>> contours;
    vector<Rect> rects;
    //找到所有相邻的点，存入contours
    findContours(dest_img, contours, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));
    for (const auto &contour : contours) {
        //根据轮廓点集合构建矩形
        Rect rect = boundingRect(contour);
        float ratio = rect.width * 1.0F / rect.height;
        cout << "rect: width = " << rect.width << " height = " << rect.height << " x = " << rect.x
             << " y = "
             << rect.y
             << " ratio = " << ratio << endl;
        if (ratio >= 9 && ratio <= 13) {//这里是按照 身份证包含的宽高比筛选矩形
            rects.push_back(rect);
            //根据轮廓区域画正方形
            rectangle(dest_img, rect, Scalar(0, 255, 255));
        }
    }

    if (rects.size() == 1) {//如果只有一个满足比例条件，则就认为它是身份证区域
        Rect rect = rects.at(0);
        found = true;
        number_img = src_img(rect);
    } else {//如果有多个（可能有其他区域或者身份证上有污渍满足这个比例），则认为是最后一个。
        int lowPoint = 0;
        Rect finalRect;
        for (auto &rect:rects) {
            if (rect.tl().y > lowPoint) {
                lowPoint = rect.tl().y;
                found = true;
                finalRect = rect;
            }
        }
        number_img = src_img(finalRect);
    }

    jobject bitmap = nullptr;


    if (found) {
        LOGE("id number found");
        bitmap = createBitmap(env, number_img, config);
    } else {
        LOGE("id number not found");
    }

    //释放资源
    src_img.release();
    dest_img.release();
    number_img.release();

    return bitmap;
}