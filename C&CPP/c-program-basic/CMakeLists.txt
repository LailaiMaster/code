cmake_minimum_required(VERSION 3.6)
project(c-program)
set(CMAKE_C_STANDARD 99)

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/01-Basic/*.c")
foreach(file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file})
endforeach()