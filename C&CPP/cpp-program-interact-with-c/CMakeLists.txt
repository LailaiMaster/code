cmake_minimum_required(VERSION 3.10)
project(Interactor)

set(CMAKE_CXX_STANDARD 11)

# c call cpp
set(SOURCE_FILES c_call_cpp/main.c c_call_cpp/CppLibrary.cpp)
add_executable(c_call_cpp ${SOURCE_FILES})

# cpp call c【1】
set(SOURCE_FILES cpp_call_c1/main.cpp cpp_call_c1/CLibrary.c)
add_executable(cpp_call_c1 ${SOURCE_FILES})

# cpp call c【2】
set(SOURCE_FILES cpp_call_c2/main.cpp cpp_call_c1/CLibrary.c)
add_executable(cpp_call_c2 ${SOURCE_FILES})
