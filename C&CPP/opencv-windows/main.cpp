#include <iostream>
#include <opencv2/opencv.hpp>

using namespace cv;

int main() {
    Mat src = imread("img/id1.jpg");
    Mat dest;
    //灰度化
    cvtColor(src, dest, COLOR_BGR2GRAY);
    //二值化
    threshold(dest, dest, 100, 255, THRESH_BINARY);

    //展示图片
    imshow("a", dest);
    //防止图片退出
    waitKey(0);
    return 0;
}
