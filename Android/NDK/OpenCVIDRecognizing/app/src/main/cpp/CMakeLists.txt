cmake_minimum_required(VERSION 3.4.1)

#native lib
add_library(native-lib SHARED native-lib.cpp)

#Lib OpenCV
add_library(opencv4java SHARED IMPORTED)
set_target_properties(opencv4java PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libopencv_java4.so)
include_directories(include)

#link
target_link_libraries(native-lib log jnigraphics opencv4java)