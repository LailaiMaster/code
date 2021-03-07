cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)

set(CMAKE_C_STANDARD 11)

set(CMAKE_INSTALL_PREFIX install)

if(APPLE)
    set(CMAKE_INSTALL_RPATH @loader_path/../lib)
elseif (UNIX)
    set(CMAKE_INSTALL_RPATH \$ORIGIN/../lib)
endif ()

include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)

# conan 会默认忽略所有的 rpath 配置，这样会导致第4节的程序无法运行，因此我们需要添加 KEEP_RPATHS 参数来启动 rpath
conan_basic_setup(KEEP_RPATHS)
# 我们也可以在这里通过 cmake 语法直接设置启动 rpath 如下：
# set(CMAKE_SKIP_RPATH OFF)

include_directories("include")

add_subdirectory(fibonacci)
add_subdirectory(factorial)

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
foreach(file ${files})
    get_filename_component(name ${file} NAME)
    add_executable(${name} ${file})

    target_link_libraries(${name}
            factorial
            fibonacci
            ${CONAN_LIBS})

    install(TARGETS ${name} RUNTIME DESTINATION bin)
endforeach()