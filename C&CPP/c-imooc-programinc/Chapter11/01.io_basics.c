#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

//=============== 输入输出流 ===============
int main() {
    //=============== 文件输入输出：FILE ===============
    //关于 mode，针对 windows 平台有个 rb 模式，表示以二进制的形式打开文件，这时 windows 不会对 \n \x1A 有什么特殊处理，而是以普通的二进制数据进行处理， 否则读到一个 \n，windows 会认为读到的是换行。
    //\n 是换行符，\x1A 是文件解释符，命令行中按 CTRL + Z 则可输入文件结束符，windows 上对二进制文件操作，如果不以 rb 模式打开文件，遇到 \x1A 数据时，会认为是读到了文件末尾。
    //描述：File access mode flag "b" can optionally be specified to open a file in binary mode. This flag has no effect on POSIX systems, but on Windows, for example, it disables special handling of '\n' and '\x1A'.
    // 具体参考 <https://en.cppreference.com/w/cpp/io/c/freopen>
    FILE *file = fopen("CMakeLists.txt", "r");

    if (file) {
        puts("Open successfully.");

        //是否有错误，若文件流出错则返回非0，否则返回0。
        int err = ferror(file);
        PRINT_INT(err);

        //是否到文件末端，如果是，则返回的是非 0。
        int eof = feof(file);
        PRINT_INT(eof);
        fclose(file);
    } else {
        PRINT_INT(errno);
        puts(strerror(errno));
        perror("fopen");//perror 打印的就是 strerror(errno) 函数返回的错误信息。
    }

    //=============== 查看错误信息 ===============
    /*查看文件 error 1-10 分别是什么意思。*/
    for (int i = 0; i < 10; ++i) {
        PRINT_INT(i);
        puts(strerror(i));
    }

    //=============== 标准输入输出 ===============
    stdout;
    stderr;
    stdin;
    return 0;
}
