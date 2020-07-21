/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */

#include <iostream>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

//OpenCV实现最基本的形态学运算之一——腐蚀，即用图像中的暗色部分“腐蚀”掉图像中的高亮部分。
void erodeImage() {
    //载入原图
    Mat srcImage = imread("img/scenery1.jpg");
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
void blurImage() {
    //【1】载入原始图
    Mat srcImage = imread("img/scenery1.jpg");
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
void cannyImage() {
    //【0】载入原始图
    Mat src_img = imread("img/id1.jpg");
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