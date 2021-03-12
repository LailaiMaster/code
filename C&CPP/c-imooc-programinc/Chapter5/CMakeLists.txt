cmake_minimum_required(VERSION 3.17)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)

set(CMAKE_C_STANDARD 99)

include_directories("include")

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
foreach(file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file} src/factorial.c)
    #传递编译宏定义，这里定义了一个 PUBLIC 级别的 DEBUG 宏定义。
    target_compile_definitions(${name} PUBLIC DEBUG)
endforeach()