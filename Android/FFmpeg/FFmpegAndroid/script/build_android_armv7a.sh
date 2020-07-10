#!/bin/bash

#临时文件夹
export TMPDIR=../temp

#NDK
export NDK=/mnt/d/windows_linux_subsystem/android-ndk-r14b
export SYSROOT=$NDK/platforms/android-19/arch-arm/
export TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
export CPU=armv7-a

#输出目录
export PREFIX=../android/FFmpeg-n2.8.8/armv7a
export ADDI_CFLAGS="-marm"

#函数
function build_one
{
./configure \
--prefix=$PREFIX \
--enable-shared \
--disable-static \
--disable-doc \
--disable-ffmpeg \
--disable-ffplay \
--disable-ffserver \
--disable-ffprobe \
--disable-postproc \
--disable-avdevice \
--disable-symver \
--enable-small \
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--target-os=linux \
--arch=arm \
--cpu=armv7-a \
--enable-cross-compile \
--sysroot=$SYSROOT \
--extra-cflags="-Os -fpic $ADDI_CFLAGS" \
--extra-ldflags="$ADDI_LDFLAGS" \
$ADDITIONAL_CONFIGURE_FLAG
make clean
make
make install
}

build_one
