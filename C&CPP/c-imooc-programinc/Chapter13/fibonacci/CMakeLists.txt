cmake_minimum_required(VERSION 3.10.2)
project(fibonacci C)

set(CMAKE_C_STANDARD 11)

if (WIN32)
    set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS 1)
endif (WIN32)

add_library(fibonacci SHARED
        fibonacci.c)

install(TARGETS fibonacci
        RUNTIME DESTINATION bin
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib/static)