#指定要求的版本
cmake_minimum_required(VERSION 3.7)

#指定项目信息
project(Sample01)

#指定C标准版本
set(CMAKE_C_STANDARD 99)

#指定需要编译的源
set(SOURCE_FILES main.c)

#测试打印一些预设变量
message("-------------------------------------------------------")
message("PROJECT_NAME = " ${PROJECT_NAME})# 工程名称
message("PROJECT_SOURCE_DIR = " ${PROJECT_SOURCE_DIR}) #工程的根目录
message("CMAKE_SOURCE_DIR = " ${CMAKE_SOURCE_DIR})# 这是包含顶级 CMakeLists.txt 的目录，即顶级源目录。
message("CMAKE_CURRENT_SOURCE_DIR = " ${CMAKE_CURRENT_SOURCE_DIR}) #当前处理的 CMakeLists.txt 所在的路径
message("CMAKE_CURRENT_LIST_DIR = " ${CMAKE_CURRENT_LIST_DIR}) #CMakeLists.txt 的完整路径
message("PROJECT_BINARY_DIR = " ${PROJECT_BINARY_DIR}) #运行 cmake 命令的目录，通常是 ${PROJECT_SOURCE_DIR}/build
message("CMAKE_CURRENT_LIST_LINE = " ${CMAKE_CURRENT_LIST_LINE}) #当前所在的行
message("-------------------------------------------------------")

#设置将源码代码编译为可执行文件
add_executable(Sample01 ${SOURCE_FILES})