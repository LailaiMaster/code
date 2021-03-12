#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

void echo() {
    while (1) {
        int next_input = getchar();
        if (next_input == EOF) {
            break;
        } else if (next_input == '\n') {
            continue;
        }
        putchar(next_input);
    }
}

void ReadFile() {
    FILE *file = fopen("CMakeLists.txt", "r");

    if (file) {
        int next_char = getc(file);
        while (next_char != EOF) {
            putchar(next_char);
            next_char = getc(file);
        }
        fclose(file);
    } else {
        PRINT_INT(errno);
        puts(strerror(errno));
        perror("fopen");
    }
}

//=============== 字符读写 ===============
/*
 * 三个函数：
 *      getchar：从 stdio 中获取一个字符，原型 int getchar(void)，等价于 getc(stdio);
 *      fgetc：从参数只当的流中获取一个字符，int fgetc(FILE *stream)
 *      getc：从参数只当的流中获取一个字符，int getc(FILE *stream)
 *
 * 如果返回的是 EOF，则表示文件读完了，可能是真的读完了或者也可能是出错了，所以还需要进一步判断。在命令行中通过 CTRL + C/Z/D （都试一下）可以输入 EOF 标识。
 *
 * 另外，相对的还有后 putchar/putc/fputc 字符输出函数。
 */
int main() {
    //echo();
    ReadFile();
    return 0;
}
