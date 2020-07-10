cmake_minimum_required(VERSION 3.12)
project(Interactor)

set(CMAKE_CXX_STANDARD 11)

#set(SOURCE_FILES c_cpp/main.c c_cpp/CppLibrary.cpp)
set(SOURCE_FILES c_cpp/CppLibrary.cpp c_cpp/main.c)
add_executable(Interactor ${SOURCE_FILES})