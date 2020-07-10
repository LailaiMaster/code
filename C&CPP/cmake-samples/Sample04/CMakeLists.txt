cmake_minimum_required(VERSION 3.7)
project(Sample04)

set(CMAKE_C_STANDARD 99)
set (MATH_VERSION_MAJOR 1)
set (MATH_VERSION_MINOR 0)

#包含当前目录，查找头文件
set(CMAKE_INCLUDE_CURRENT_DIR ON)

# 设置一个变量，用于标识是否使用自己的 MathFunctions 库
#option (USE_MYMATH "Use provided math implementation" OFF)
option (USE_MYMATH "Use provided math implementation" ON)

# 加入一个配置头文件，用于处理 CMake 对源码的设置
configure_file (
        "${PROJECT_SOURCE_DIR}/config.h.in"
        "${PROJECT_BINARY_DIR}/config.h"
)

message("time 1 EXTRA_LIBS = " ${EXTRA_LIBS})

# 如果使用自己的 Math 库，就加入我们的 MathLib 库
if (USE_MYMATH)
    include_directories ("${PROJECT_SOURCE_DIR}/Libs")
    add_subdirectory (Libs)
    #    这里是往 EXTRA_LIBS 中添加 MathLib。就像 Linux 中的 export PATH=$PATH:/path/to/dir 一样。
    #    事实上这里并没有 EXTRA_LIBS，那就相当于将 MathLib 设置为 EXTRA_LIBS。
    set (EXTRA_LIBS ${EXTRA_LIBS} MathLib)
endif (USE_MYMATH)

message("time 2 EXTRA_LIBS = " ${EXTRA_LIBS})

#构建
aux_source_directory(. SOURCE_FILES) # 包含当前目录所有源代码
add_executable(Sample4 ${SOURCE_FILES} )# 编译为可执行文件
target_link_libraries (Sample4  ${EXTRA_LIBS})