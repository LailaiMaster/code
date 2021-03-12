#include <stdio.h>

#include "fibonacci/fibonacci.h"
#include "factorial/factorial.h"

//=============== 静态库与动态库，使用 CMake 导入库文件 ===============
/*
 * 将 factorial 和 fibonacci 拆成两个独立的库模块【有各自的 CmakeList.txt 文件】，一个编译成动态库，一个编译成静态库，对应文件夹为：factorial 和 fibonacci。
 *
 *  直接运行 04.library_with_cmake 会报错，因为找不到库文件，这与 Clion 的工作方式有关，默认其不会执行 install 命令，所以：
 *      factorial 和 fibonacci 编译后还是在 xxx-build-xxx/xxx 目录下，而 04.library_with_cmake.exe 也在 xxx-build-xxx/xxx 目录下。
 *  因此，还需要手动在 【Build-->Install】工具栏，执行一下 install 命令，就可以看到在 xxx-build-xxx/xxx/install 目录下生成了可以自行文件已经相关库文件。【install 目录是在 CmakeList.text 中指定的】
 *
 * --------------------------Windows 平台------------------------------------
 *  使用 MinGw 编译出 Windows 平台的可执行文件路径为：
 *      install
 *              bin
 *                  libfibonacci.dll【动态库】
 *                  04.library_with_cmake.c.exe
 *              lib/static
 *                  libfibonacci.dll.a 【动态库需要的导入库】
 *
 * --------------------------Linux 平台------------------------------------
 *
 *  使用 WSL 编译出 Linux 平台的可执行文件路径为：
 *      install
 *              bin
 *                  04.library_with_cmake.c
 *              lib
 *                  libfibonacci.so【动态库】
 *
 *  bin 目录中的可执行后缀为 c 是因为我们没有指定，其实它已经是可执行文件。可与在 wsl 命令行用 file 目录查看文件类型：
 *      执行 file 04.library_with_cmake.c 命令，输入如下，发现是 elf 文件。
 *               04.library_with_cmake.c: ELF 64-bit LSB shared object, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2,
 *               for GNU/Linux 3.2.0, BuildID[sha1]=f7386f2bc8f120503a72564bf1c696c197b4762e, with debug_info, not stripped
 *      使用  ldd 04.library_with_cmake.c 命令，可以查看其包含那些库：
 *              linux-vdso.so.1 (0x00007fffdbf43000)
 *              libfibonacci.so => /mnt/d/code/code/C&CPP/c-imooc-programinc/Chapter13/cmake-build-wsl/install/bin/./../lib/libfibonacci.so (0x00007ffcc8b10000)
 *              libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007ffcc8710000)
 *              /lib64/ld-linux-x86-64.so.2 (0x00007ffcc9000000)
 *      使用 readelf -a 04.library_with_cmake.c  可以发现，有如下内容：
 *              Dynamic section at offset 0xda0 contains 29 entries:
 *                 Tag        Type                         Name/Value
 *                0x0000000000000001 (NEEDED)             Shared library: [libfibonacci.so]
 *                0x0000000000000001 (NEEDED)             Shared library: [libc.so.6]
 *                0x000000000000001d (RUNPATH)            Library runpath: [$ORIGIN/../lib]
 *       其中，Library runpath: [$ORIGIN/../lib] 就很重要，如果没有这个，需要的 so 库它就会去其他目录找了，而不从当前目录开始找。
 *
 */
int main(void) {

    printf("Fibonacci(5): %d\n", Fibonacci(5));
    printf("Factorial(5): %d\n", Factorial(5));

    return 0;
}