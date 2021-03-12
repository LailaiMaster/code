cmake_minimum_required(VERSION 3.10.2)
project(fibonacci C)

set(CMAKE_C_STANDARD 11)

# Windows 平台还需要一个导入库，因此设置 CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS，才会生成一个导入库。
if (WIN32)
    set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS 1)
endif (WIN32)

# 编译成动态库
add_library(fibonacci SHARED fibonacci.c)

# install 是将动态库和可执行文件 install 到一起，否则可执行程序找不到动态库【下面根据不同平台 install 到不同的目录】。
install(TARGETS fibonacci
        # RUNTIME 是运行时需要用到的东西，Windows 说包括 exe/dll 文件，windows 平台会 install 到 bin 目录。
        RUNTIME DESTINATION bin
        # Linux/Mac 平台则将 so 库作为动态库文件，放入 library 里面
        LIBRARY DESTINATION lib
        # Windows 平台的需要的导入库放入 lib/static 目录下，这些都是个平台的规则。
        ARCHIVE DESTINATION lib/static)