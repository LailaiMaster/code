#include <stdio.h>
#include <io_utils.h>
#include <time_utils.h>
#include <locale.h>

#if defined(__APPLE__) || defined(__linux__)
#  include <unistd.h>//posix
#elif defined(_WIN32)
#  include <io.h>
#endif

void RedirectStdout(char const *filename) {
    static int saved_stdout_no = -1;

    if (filename) {
        if (saved_stdout_no == -1) {
            //保留 stdout 副本
            saved_stdout_no = dup(fileno(stdout));
        }
        //先刷新一下标准输出
        fflush(stdout);
        //stdout 指向新打开的文件
        freopen(filename, "a", stdout);
    } else {
        if (saved_stdout_no != -1) {
            //先刷新一下 stdout
            fflush(stdout);
            //让 stdout 指向原来的 stdout。
            dup2(saved_stdout_no, fileno(stdout));
            close(saved_stdout_no);
            saved_stdout_no = -1;
        }
    }
}


void redirect_stdout() {
    //原有的 stdout 会被关闭，无法再通过常规方法打开。
    freopen("output.log", "a", stdout);
    puts("This will be written to file 'output.log'");
    fclose(stdout);
}

//=============== 重定向标准输入输出 ===============
/*
 * 标准库：
 *
 *  1. freopen 不好用。
 *
 *posix 标准：
 *
 *  1. int dup(int fildes);//复制一个文件描述符，返回一个新的文件描述符，指向入参文件描述符指向的文件。
 *  2. int dup2(int fildes, int fildes2);//让 fildes2 指向 fildes。如果 fildes2 已经指向了其他文件，则先将其关闭。
 *  3. int fileno(FILE *stream);//获取文件描述符。
 *  4. close(int fildes);//关闭文件描述符。
 *
 *  命令行操作 python x.py > output.log 2>&1 的含义是
 *      2 表示 stderr
 *      1 表示 stdout
 *      所以，上面命令表示标准输出和标准错误输出都输出到 output.log 中。
 */
int main() {
    //redirect_stdout();

    puts("1");
    RedirectStdout("output.log");
    puts("2");
    RedirectStdout(NULL);
    puts("3");
    RedirectStdout("output.log");
    puts("4");
    RedirectStdout(NULL);
    puts("end");

    // python x.py > output.log 2>&1
    return 0;
}
