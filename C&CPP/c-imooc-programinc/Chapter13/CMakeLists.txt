cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)
set(CMAKE_C_STANDARD 11)

#===============================================================================Cmake 导入动态/静态库配置
# 对应 04.library_with_cmake 实例。
#这里设置 install 的路径，CMAKE_INSTALL_PREFIX 是内置的 install 路径。
# 我们将其设置为 install 字符串【这里 install 其实是一个相对路径】，那么 CMAKE_INSTALL_PREFIX 指向的就是 xxx-build---/install 目录。
set(CMAKE_INSTALL_PREFIX install)
message("CMAKE_INSTALL_PREFIX = ${CMAKE_INSTALL_PREFIX}")

if (APPLE)#如果是 Mac，需要设置 CMAKE_INSTALL_RPATH，load_path 就是可执行程序的位置
    set(CMAKE_INSTALL_RPATH @loader_path/../lib)
elseif (UNIX)#如果是 UNIX/Linux，需要设置 CMAKE_INSTALL_RPATH，$ORIGIN 就是可执行程序的位置
    set(CMAKE_INSTALL_RPATH \$ORIGIN/../lib)
endif () # Windows 不用配置，CMAKE_INSTALL_RPATH 默认就包含可执行文件的路径。

# 添加两个 cmake 子模块
add_subdirectory(fibonacci)
add_subdirectory(factorial)
#===============================================================================Cmake 导入动态/静态库配置


#===============================================================================导入 conan 的配置
# conan step1：在 cmake-build.xxx 中 执行 conan install .. 命令之后，会生成 conanbuildinfo.cmake 文件，
#                         然后在这里导入 conan 生成的配置文件，CMAKE_BINARY_DIR 就是 cmake-build-xxx 目录的路径，而 conan 生成的配置文件就在 build 目录下。
include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
# conan step2：conan 会默认忽略所有的 rpath 配置，这样会导致第4节的程序无法运行，因此我们需要添加 KEEP_RPATHS 参数来启动 rpath
conan_basic_setup(KEEP_RPATHS)
# 我们也可以在这里通过 cmake 语法直接设置启动 rpath 如下：
# set(CMAKE_SKIP_RPATH OFF)
#===============================================================================导入 conan 的配置


#===============================================================================构建并链接
include_directories("include")
# 获取所有的 .c 文件
file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")

foreach (file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file})

    # 链接所有的库文件：
    #   factorial fibonacci 是两个本地 cmake 子模块
    #   conan step3：链接 conan 生成的库，CONAN_LIBS 引用的就是conan 生成的库。
    target_link_libraries(${name} factorial fibonacci ${CONAN_LIBS})

    # 将可执行文件 install 到 bin 目录下【install 就是 copy 文件的过程，要执行 build-->install 才会运行 install 过程】
    install(TARGETS ${name} RUNTIME DESTINATION bin)

endforeach ()
#===============================================================================构建并链接