#include <stdio.h>

#include "mathutils/include/fibonacci.h"
#include "mathutils/include/factorial.h"

//=============== 静态库与动态库【命令行打包动态库】 ===============
/*
 本节使用命令行编译 mathutils 下的两个 c 文件为动态库，然后编译 02.static_library.c 为可执行文件。【基于 MinGW 演示】具体步骤为：

    1. 命令行进入到 mathutils 目录下：
        执行
            gcc -shared -fPIC factorial.c fibonacci.c -o libmathutils.dll 命令【Linux平台，-fPIC 指令用于生成与地址无关的的动态库】
            gcc -shared -fPIC -Wl,--out-implib,libmathutils.a factorial.c fibonacci.c -o libmathutils.dll 命令【Windows平台】
        Windows 平台会得到两个文件：libmathutils.a 和 libmathutils.dll。
            其中 libmathutils.a 是导入库，libmathutils.dll 是动态链接库。
        之后可以使用 objdump -t libmathutils.dll 命令查看动态链接库里面的符号。

    2. 命令行进入 Chapter13 目录下：
        执行
            gcc 03.shared_library.c -Lmathutils -lmathutils -o dy.exe 【L 用于指定动态库的目录，l 用于指定链接那个动态库】
        命令，得到 dy.exe 可执行文件【用 file 命令可以查看 a.exe 文件的类型】

        此时在当前目录执行 dy.exe 会报错，提示找不到 libmathutils.dll，使用 ldd dy.exe 命令发现里面确实没有找到  libmathutils.dll 库的路径。
        然后进入 mathutils 目录下，执行 ../dy.exe 发现程序正常执行。这就是 Windows 系统的逻辑，现在当前目录找动态库，如果找不到则去配置的 Path 目录下找动态库。
        而 Linux 则不同，可以通过参数指定查找动态库的路径。
 */
int main(void) {

  printf("Fibonacci(5): %d\n", Fibonacci(5));
  printf("Factorial(5): %d\n", Factorial(5));

  return 0;
}