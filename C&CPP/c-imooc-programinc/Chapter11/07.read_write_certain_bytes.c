#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

void Echo() {
#define BUFFER_SIZE 4
    //char buffer[BUFFER_SIZE];
    int buffer[BUFFER_SIZE];//一个整数可以存四个 char

    while (1) {
        //返回读取的数量
        size_t bytes_read = fread(buffer, sizeof(buffer[0]), BUFFER_SIZE, stdin);
        PRINT_INT_ARRAY(buffer, 4);

        if (bytes_read < BUFFER_SIZE) {//没有读到期望的那么多，可能出来问题或者是读完了。
            if (feof(stdin)) {//读完了
                puts("EOF");
                fwrite(buffer, sizeof(buffer[0]), bytes_read, stdout);
            } else if (ferror(stdin)) {//出错了
                perror("Error read from stdin");
            }
            break;
        }

        fwrite(buffer, sizeof(buffer[0]), BUFFER_SIZE, stdout);
    }
}

//=============== 读写字节：fread /fwrite ===============
/*
 * 如果仅仅是读写二进制，fread/fwrite 的 api 完全可以设计成只接收 char* 类型的数据，但是它们接收 void* 类型的数据，
 * 这样就给了开发者很大的灵活性，只要按规则读写，就可以一次读写任何类型的数据了。
 */
int main() {
    Echo();
    return 0;
}
