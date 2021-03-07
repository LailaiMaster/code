cmake_minimum_required(VERSION 3.10.2)

project(07.call_from_java C)

set(CMAKE_CXX_STANDARD 11)

set(CMAKE_INSTALL_PREFIX install)

find_package(JNI REQUIRED)

include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

if (WIN32)
    set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS 1)
endif (WIN32)

add_library(07.call_from_java SHARED fibonacci.c)

install(TARGETS 07.call_from_java
        RUNTIME DESTINATION bin
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib/static)