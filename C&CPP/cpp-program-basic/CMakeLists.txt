cmake_minimum_required(VERSION 3.7)
project(cplusplus-program)

set(CMAKE_CXX_STANDARD 11)

#禁止编译器优化(测试对象移动)
#add_compile_options(-fno-elide-constructors)

set(SOURCE_FILES posix-pthread/posix-pthread.cpp)
add_executable(CPP_Basic ${SOURCE_FILES})
