#include <stdio.h>

#include "mathutils/include/fibonacci.h"
#include "mathutils/include/factorial.h"

//=============== 静态库与动态库【命令行打包静态库】 ===============
/*
 本节使用命令行编译 mathutils 下的两个 c 文件为静态库，然后编译 02.static_library.c 为可执行文件。【基于 MinGW 演示】具体步骤为：

    1. 命令行进入到 mathutils 目录下：
        执行 gcc -c factorial.c fibonacci.c 命令，得到汇编后的 factorial.o 和 fibonacci.o 文件。【如果是 Linux 平台，那就是 .elf 文件】
        指定  ar rcs libmathutils.a factorial.o fibonacci.o 命令，得到打包后的 libmathutils.a 静态链接库【此时可以执行 ar t libmathutils.a 命令查看 libmathutils.a 内部包含什么文件】。
    2. 命令行进入 Chapter13 目录下：
        执行 gcc 02.static_library.c mathutils\libmathutils.a 命令，得到可执行文件 a.exe。【用 file 命令可以查看 a.exe 文件的类型】

    当然，可以直接 gcc 02.static_library.c mathutils\factorial.o mathutils\fibonacci.o，但是如果 .o 文件太多就显得很麻烦，所以还是先用 ar 将所有汇编后端文件打包成静态库。
 */
int main(void) {

    printf("Fibonacci(5): %d\n", Fibonacci(5));
    printf("Factorial(5): %d\n", Factorial(5));

    return 0;
}