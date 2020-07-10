cmake_minimum_required(VERSION 3.15)
project(FFmpegWindows_Clion)

set(CMAKE_CXX_STANDARD 14)

#能够在编码的时候使用ffmpeg的函数
include_directories(include)
# 设置库的查找目录
link_directories(libs)

#可执行文件
add_executable(FFmpegWindows_Clion ffmpeg-main.cpp)

#设置编译链接的库 ffmpeg
target_link_libraries( FFmpegWindows_Clion  avutil avcodec avfilter  avdevice avformat postproc swresample )