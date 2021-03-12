cmake_minimum_required(VERSION 3.10.2)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId} C)

set(CMAKE_C_STANDARD 11)

include_directories("include")

if (WIN32)
    set(DEPS gtk+-3.0)
elseif (APPLE)
    set(DEPS gtk+-3.0 glib-2.0)
else()
    set(DEPS gtk+-3.0)
endif ()

# Use the package PkgConfig to detect GTK+ headers/library files
FIND_PACKAGE(PkgConfig REQUIRED)

foreach (dep ${DEPS})
    PKG_CHECK_MODULES(${dep} REQUIRED ${dep})
    INCLUDE_DIRECTORIES(${${dep}_INCLUDE_DIRS})
    LINK_DIRECTORIES(${${dep}_LIBRARY_DIRS})
endforeach ()

if (WIN32)
    set(CMAKE_C_FLAGS_RELEASE -mwindows)
endif()

file(GLOB files "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
add_executable(calculator ${files})

foreach (dep ${DEPS})
    target_link_libraries(calculator ${${dep}_LIBRARIES})
endforeach ()