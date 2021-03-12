cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)

set(CMAKE_C_STANDARD 11)

include_directories("include")
include_directories("tinycthread/include")

if (!MSVC)
    set(CMAKE_C_FLAGS "-O3")
endif ()

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
foreach (file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file} tinycthread/tinycthread.c)

    #如果是 Unix 和 Linux 平台，要主动链接 pthread 库【默认不链接】。
    if (UNIX OR Linux)
        target_link_libraries(${name} pthread)
    endif ()
endforeach ()