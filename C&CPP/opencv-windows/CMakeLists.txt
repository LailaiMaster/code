cmake_minimum_required(VERSION 3.12)
project(opencv_windows)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")


include_directories("D:/code/opencv-4.1.1/mingw-build/install/include")
link_directories("D:/code/opencv-4.1.1/mingw-build/install/x64/mingw/lib" )

add_executable(opencv_windows main.cpp)
target_link_libraries(opencv_windows libopencv_world411)