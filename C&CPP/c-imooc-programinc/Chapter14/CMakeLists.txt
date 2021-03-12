cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId})

set(CMAKE_C_STANDARD 11)

# 找到 mingw 的包管理工具 pkg-config
FIND_PACKAGE(PkgConfig REQUIRED)

# gtk-step1：通过找到 pkgConfig，命名为 GTK3【组件名通过在 mingw 窗口使用 pkg-config 命令查看】。
PKG_CHECK_MODULES(GTK3 REQUIRED gtk+-3.0)
# gtk-step2：导入 gtk 的头文件
INCLUDE_DIRECTORIES(${GTK3_INCLUDE_DIRS})
# gtk-step3：链接 gtk 库文件
LINK_DIRECTORIES(${GTK3_LIBRARY_DIRS})
# gtk-step4：导入 gtk 的其他编译参数
ADD_DEFINITIONS(${GTK3_CFLAGS_OTHER})

# Windows 上，gtk_builder_connect_signals 函数需要 gmodule-export-2.0 模块【start】
PKG_CHECK_MODULES(GMODULE REQUIRED gmodule-export-2.0)
INCLUDE_DIRECTORIES(${GMODULE_INCLUDE_DIRS})
LINK_DIRECTORIES(${GMODULE_LIBRARY_DIRS})
# Windows 上，gtk_builder_connect_signals 函数需要 gmodule-export-2.0 模块【end】

include_directories("include")

# 启动程序的时候不展示命令行窗口。
if (WIN32)
    set(CMAKE_C_FLAGS_RELEASE -mwindows)
    # set(CMAKE_C_FLAGS_DEBUG -mwindows)
endif ()

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
foreach (file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file})
    # gtk-step5：将 gtk 链接给我们的工程
    target_link_libraries(${name} ${GTK3_LIBRARIES})
    # Windows 上，gtk_builder_connect_signals 函数需要 gmodule-export-2.0 模块
    target_link_libraries(${name} ${GMODULE_LIBRARIES})
endforeach ()

# 第九课用的 cpp。
set(CMAKE_CXX_STANDARD 11)
add_executable(09.oop.cpp 09.oop.cpp)