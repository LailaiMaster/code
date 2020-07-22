/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */

#ifndef OPENCV_WINDOWS_BASE_IMAGE_PROCESS_H
#define OPENCV_WINDOWS_BASE_IMAGE_PROCESS_H

#include "string"

namespace cv_base {
    void erodeImage(std::string& path);
    void blurImage(std::string& path) ;
    void cannyImage(std::string& path);
    void findIdentityCardNumber(std::string& path);
    void findIdentityCardNumber2(std::string& path);
}

#endif
