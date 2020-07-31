/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */

#include <opencv2/opencv.hpp>
#include "base_image_process.h"

namespace cv_base {

    using namespace cv;
    using namespace std;

    #define DEFAULT_CARD_WIDTH 640
    #define DEFAULT_CARD_HEIGHT 400
    #define FIX_IDCARD_SIZE Size(DEFAULT_CARD_WIDTH,DEFAULT_CARD_HEIGHT)

//OpenCV实现最基本的形态学运算之一——腐蚀，即用图像中的暗色部分“腐蚀”掉图像中的高亮部分。
    void erodeImage(std::string &path) {
        //载入原图
        Mat srcImage = imread(path);
        //显示原图
        imshow("【原图】腐蚀操作", srcImage);
        //进行腐蚀操作
        //getStructuringElement函数的返回值为指定形状和尺寸的结构元素（内核矩阵
        Mat element = getStructuringElement(MORPH_RECT, Size(15, 15));
        Mat dstImage;
        erode(srcImage, dstImage, element);
        //显示效果图
        imshow("【效果图】腐蚀操作", dstImage);
        waitKey(0);
    }

//用OpenCV对图像进行均值滤波操作，模糊一幅图像
    void blurImage(std::string &path) {
        //【1】载入原始图
        Mat srcImage = imread(path);
        //【2】显示原始图
        imshow("均值滤波【原图】", srcImage);
        //【3】进行均值滤波操作
        Mat dstImage;
        blur(srcImage, dstImage, Size(10, 10));
        //【4】显示效果图
        imshow("均值滤波【效果图】", dstImage);

        waitKey(0);
    }

//用OpenCV进行canny边缘检测：载入图像，并将其转成灰度图，再用blur函数进行图像模糊以降噪，然后用canny函数进行边缘检测，最后进行显示
    void cannyImage(std::string &path) {
        //【0】载入原始图
        Mat src_img = imread(path);
        imshow("【原始图】Canny边缘检测", src_img);  //显示原始图
        Mat dst_image, edge, gray_image; //参数定义
        //【1】创建与src同类型和大小的矩阵(dst)
        dst_image.create(src_img.size(), src_img.type());
        //【2】将原图像转换为灰度图像
        cvtColor(src_img, dst_image, COLOR_RGB2GRAY);
        //【3】先使用 3x3内核来降噪
        blur(dst_image, edge, Size(3, 3));
        //【4】运行Canny算子
        Canny(edge, edge, 3, 9, 3);
        //【5】显示效果图
        imshow("【效果图】Canny边缘检测", edge);

        waitKey(0);
    }

    void findIdentityCardNumber(std::string &path) {
        Mat src = imread(path);
        Mat dest;
        Mat number_img;
        bool found = false;

        //变为 640*400 的size，这是身份证的size。
        resize(src, src,FIX_IDCARD_SIZE);
        //灰度化
        cvtColor(src, dest, COLOR_BGR2GRAY);
        //二值化
        threshold(dest, dest, 100, 255, THRESH_BINARY);
        //图形膨胀
        Mat erodeElement = getStructuringElement(MORPH_RECT, Size(20, 10));
        erode(dest, dest, erodeElement);

        //轮廓检测
        vector<vector<Point>> contours;
        vector<Rect> rects;
        //找到所有相邻的点，存入contours
        findContours(dest, contours, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));
        for (const auto &contour : contours) {
            //根据轮廓点集合构建矩形
            Rect rect = boundingRect(contour);
            float ratio = rect.width * 1.0F / rect.height;
            cout << "rect: width = " << rect.width << " height = " << rect.height << " x = " << rect.x << " y = "
                 << rect.y
                 << " ratio = " << ratio << endl;
            if (ratio >= 9 && ratio <= 13) {//这里是按照 身份证包含的宽高比筛选矩形
                rects.push_back(rect);
                //根据轮廓区域画正方形
                rectangle(dest, rect, Scalar(0, 255, 255));
            }
        }

        if (rects.size() == 1) {//如果只有一个满足比例条件，则就认为它是身份证区域
            Rect rect = rects.at(0);
            found = true;
            number_img = src(rect);
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
            number_img = src(finalRect);
        }

        //展示图片
        imshow("1", src);
        imshow("2", dest);
        if (found) {
            imshow("3", number_img);
        }

        //防止图片退出
        waitKey(0);

        //释放资源
        src.release();
        dest.release();
        number_img.release();
    }

    void findIdentityCardNumber2(std::string &path) {
        Mat src = imread(path);
        // 结果图
        Mat dst;
        // 显示原图
        imshow("原图", src);

        cvtColor(src, dst, COLOR_RGB2GRAY);
        // 高斯模糊，主要用于降噪
        GaussianBlur(dst, dst, Size(3, 3), 0);
        imshow("GaussianBlur图", dst);
        // 二值化图，主要将灰色部分转成白色，使内容为黑色
        threshold(dst, dst, 165, 255, THRESH_BINARY);
        imshow("threshold图", dst);
        // 中值滤波，同样用于降噪
        medianBlur(dst, dst, 3);
        imshow("medianBlur图", dst);
        // 腐蚀操作，主要将内容部分向高亮部分腐蚀，使得内容连接，方便最终区域选取
        erode(dst, dst, Mat(9, 9, CV_8U));
        imshow("erode图", dst);

        //定义变量
        vector<vector<Point>> contours;
        vector<Vec4i> hierarchy;
        findContours(dst, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE);

        Mat result;

        for (int i = 0; i < hierarchy.size(); i++) {
            Rect rect = boundingRect(contours.at(i));
            rectangle(src, rect, Scalar(255, 0, 255));
            // 定义身份证号位置大于图片的一半，并且宽度是高度的6倍以上
            if (rect.y > src.rows / 2 && rect.width / rect.height > 6) {
                result = src(rect);
                imshow("身份证号", result);
            }
        }

        imshow("轮廓图", src);

        waitKey(0);
    }

}