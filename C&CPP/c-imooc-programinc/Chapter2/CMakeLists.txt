cmake_minimum_required(VERSION 3.17)
# 活动当前项目目录名，存储到 ProjectId
get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
# 替换掉下划线
string(REPLACE " " "_" ProjectId ${ProjectId})
# 定义一个项目
project(${ProjectId} C)
# 设置 C 标准
set(CMAKE_C_STANDARD 11)
# 获取当前项目目录下所有 .c 文件
file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
# 将每个文件添加为可执行程序
foreach (file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file})
endforeach ()