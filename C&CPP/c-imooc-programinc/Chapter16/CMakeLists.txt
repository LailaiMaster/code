cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)

set(CMAKE_C_STANDARD 11)

include_directories("include")

include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
conan_basic_setup()
#
#if (WIN32)
#    set(DEPS gtk+-3.0 libzstd)
#elseif (APPLE)
#    set(DEPS gtk+-3.0 glib-2.0)
#else()
#    set(DEPS gtk+-3.0)
#endif ()

## Use the package PkgConfig to detect GTK+ headers/library files
#FIND_PACKAGE(PkgConfig REQUIRED)
#foreach (dep ${DEPS})
#    PKG_CHECK_MODULES(${dep} REQUIRED ${dep})
#endforeach ()
##PKG_CHECK_MODULES(ZSTD REQUIRED libzstd)
#set(CMAKE_C_FLAGS_RELEASE -mwindows)
## Setup CMake to use GTK+, tell the compiler where to look for headers
## and to the linker where to look for libraries
#foreach (dep ${DEPS})
#    INCLUDE_DIRECTORIES(${${dep}_INCLUDE_DIRS})
#    LINK_DIRECTORIES(${${dep}_LIBRARY_DIRS})
#endforeach ()

INCLUDE_DIRECTORIES(include)

## Add other flags to the compiler
#ADD_DEFINITIONS(${GTK3_CFLAGS_OTHER})

# list all c files in current dir recursively.
file(GLOB_RECURSE files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
list(FILTER files EXCLUDE REGEX "${CMAKE_CURRENT_SOURCE_DIR}/cmake.*" )

add_executable(downloader ${files})
target_link_libraries(downloader ${CONAN_LIBS})

#foreach (dep ${DEPS})
#    target_link_libraries(downloader ${${dep}_LIBRARIES})
#endforeach ()