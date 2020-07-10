cmake_minimum_required(VERSION 3.4.1)

# Base DIR
set(cpp_dir ${CMAKE_SOURCE_DIR}/src/main/cpp)

# FFmpeg
set(FFmpegLibDir ffmpeglib2_8_8)
add_library(avcodec SHARED IMPORTED )
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libavcodec-56.so)

add_library(avfilter SHARED IMPORTED)
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libavfilter-5.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libavformat-56.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libavutil-54.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libswresample-1.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION  ${cpp_dir}/${FFmpegLibDir}/${ANDROID_ABI}/libswscale-3.so)

# libyuv

set(ly_src_dir ${cpp_dir}/libyuv/source/)
set(ly_inc_dir ${cpp_dir}/libyuv/include)

set(ly_source_files
  ${ly_src_dir}/compare.cc
  ${ly_src_dir}/compare_common.cc
  ${ly_src_dir}/compare_neon.cc
  ${ly_src_dir}/compare_gcc.cc
  ${ly_src_dir}/compare_win.cc
  ${ly_src_dir}/convert.cc
  ${ly_src_dir}/convert_argb.cc
  ${ly_src_dir}/convert_from.cc
  ${ly_src_dir}/convert_from_argb.cc
  ${ly_src_dir}/convert_jpeg.cc
  ${ly_src_dir}/convert_to_argb.cc
  ${ly_src_dir}/convert_to_i420.cc
  ${ly_src_dir}/cpu_id.cc
  ${ly_src_dir}/mjpeg_decoder.cc
  ${ly_src_dir}/mjpeg_validate.cc
  ${ly_src_dir}/planar_functions.cc
  ${ly_src_dir}/rotate.cc
  ${ly_src_dir}/rotate_argb.cc
  ${ly_src_dir}/rotate_mips.cc
  ${ly_src_dir}/rotate_neon.cc
  ${ly_src_dir}/row_any.cc
  ${ly_src_dir}/row_common.cc
  ${ly_src_dir}/row_mips.cc
  ${ly_src_dir}/row_neon.cc
  ${ly_src_dir}/row_gcc.cc
  ${ly_src_dir}/row_win.cc
  ${ly_src_dir}/scale.cc
  ${ly_src_dir}/scale_any.cc
  ${ly_src_dir}/scale_argb.cc
  ${ly_src_dir}/scale_common.cc
  ${ly_src_dir}/scale_mips.cc
  ${ly_src_dir}/scale_neon.cc
  ${ly_src_dir}/scale_gcc.cc
  ${ly_src_dir}/scale_win.cc
  ${ly_src_dir}/video_common.cc
)

set(ly_header_files
  ${ly_inc_dir}/libyuv/basic_types.h
  ${ly_inc_dir}/libyuv/compare.h
  ${ly_inc_dir}/libyuv/convert.h
  ${ly_inc_dir}/libyuv/convert_argb.h
  ${ly_inc_dir}/libyuv/convert_from.h
  ${ly_inc_dir}/libyuv/convert_from_argb.h
  ${ly_inc_dir}/libyuv/cpu_id.h
  ${ly_inc_dir}/libyuv/planar_functions.h
  ${ly_inc_dir}/libyuv/rotate.h
  ${ly_inc_dir}/libyuv/rotate_argb.h
  ${ly_inc_dir}/libyuv/row.h
  ${ly_inc_dir}/libyuv/scale.h
  ${ly_inc_dir}/libyuv/scale_argb.h
  ${ly_inc_dir}/libyuv/scale_row.h
  ${ly_inc_dir}/libyuv/version.h
  ${ly_inc_dir}/libyuv/video_common.h
  ${ly_inc_dir}/libyuv/mjpeg_decoder.h
)

include_directories(${cpp_dir}/ffmpeglib2_8_8_include)
include_directories(${ly_inc_dir})

# Android log
find_library(log-lib  log)

# 构建Lib
add_library(
native-lib SHARED
${cpp_dir}/decode.c
${cpp_dir}/queue.c
${cpp_dir}/push_stream.c
${cpp_dir}/player.c
${cpp_dir}/sync_render.c
${cpp_dir}/sync_sound.c
${cpp_dir}/ffmpeg_info.c
${ly_source_files})

target_link_libraries(native-lib avcodec avfilter avformat avutil swresample swscale   ${log-lib} android)
