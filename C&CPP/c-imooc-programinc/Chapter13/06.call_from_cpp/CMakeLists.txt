cmake_minimum_required(VERSION 3.10.2)
project(06.call_from_cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(mathutils/include)

add_executable(06.call_from_cpp 06.call_from_cpp.cpp mathutils/factorial.cpp mathutils/fibonacci.c)