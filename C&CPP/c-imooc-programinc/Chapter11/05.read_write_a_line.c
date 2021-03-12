#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

void ReadFile() {
    FILE *file = fopen("CMakeLists.txt", "r");
    if (file) {
        char buffer[BUFSIZ];
        while (fgets(buffer, BUFSIZ, file)) {
            printf(buffer);
        }
        fclose(file);
    } else {
        PRINT_INT(errno);
        puts(strerror(errno));
        perror("fopen");
    }
}

//=============== 读写一行字符：fgets ===============
void Echo() {
    char buffer[4];
    while (1) {
        //fgets 读取成功返回 buffer 本身，否则返回 null。
        //fgets 会自动处理最后输入的换行符。
        if (!fgets(buffer, 4, stdin)) {
            break;
        }
        //puts(buffer);//puts 会自动加入换行符
        printf("%s", buffer);
    }
}

//=============== 读写一行字符 ===============
/*
 * 1. gets 不安全，会从 stdin 不断地读，直到读到换行符，长度不可控，所以设置多长的 buffer 都不安全。C11 后 gets 被一移除，取而代之的是 gets_s。
 * 2. gets/fgets/puts/fpus 等都只用于文本内容的读写，如果是二进制数据的读写，不要用这些函数。
 */
int main() {
    Echo();
    //ReadFile();
    return 0;
}
