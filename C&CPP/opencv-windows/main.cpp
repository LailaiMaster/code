#include <iostream>
#include <opencv2/opencv.hpp>
#include "base/base_image_process.h"

using namespace cv;
using namespace std;

int main() {
    string path_id1("img/scenery1.jpg");
    cv_base::erodeImage(path_id1);
    //cv_base::blurImage();
    //cv_base::cannyImage(path_id1);
    //cv_base::findIdentityCardNumber(path_id1);
    //cv_base::findIdentityCardNumber2(path_id1);
    return EXIT_SUCCESS;
}
