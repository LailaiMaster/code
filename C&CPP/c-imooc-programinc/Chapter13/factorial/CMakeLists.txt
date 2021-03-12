cmake_minimum_required(VERSION 3.10.2)
project(factorial C)

set(CMAKE_C_STANDARD 11)

# 编译成动态库
add_library(factorial STATIC factorial.c)