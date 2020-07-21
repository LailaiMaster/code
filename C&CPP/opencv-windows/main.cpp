#include <iostream>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

void findIdentityCardNumber() {
    Mat src = imread("img/id1.jpg");
    Mat dest;
    Mat number_img;
    bool found = false;

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
        cout << "rect: width = " << rect.width << " height = " << rect.height << " x = " << rect.x << " y = " << rect.y
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

extern void cannyImage();

int main() {
    //erodeImage();
    //blurImage();
    cannyImage();
    //findIdentityCardNumber();
    return 0;
}
