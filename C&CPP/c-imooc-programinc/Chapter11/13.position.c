#include <stdio.h>
#include <io_utils.h>
#include <locale.h>

//=============== 输入输出流的位置：fseek ===============
int main() {
    /*
     *1. 如果 fopen 以文本的方式打开，则 fseek offset 参数的传参只能是 0 或者 ftell 的返回值。
     * 2. fseek 仅适用于窄字节读写，因为其按字节数设置位置，如果遇到宽字符就可能把宽字符给拆开了，对于宽字符，应该使用 fsetpos/fgetpos 函数。
     * 3. stdio/stdout 不支持 fseek。
     */
    FILE *file = fopen("CMakeLists.txt", "rb");
    long position = ftell(file);
    PRINT_LONG(position);
    char buffer[128];
    fread(buffer, 1, 128, file);
    PRINT_LONG(ftell(file));
    fseek(file, 10, SEEK_SET);//从 0 开始偏移
    PRINT_LONG(ftell(file));
    fseek(file, 10, SEEK_CUR);//从当前位置开始偏移
    PRINT_LONG(ftell(file));
    fseek(file, -10, SEEK_END);//从末端位置开始偏移
    PRINT_LONG(ftell(file));
    fseek(file, 0, SEEK_END);//虽然跳到了文件末尾，可以得到文件大小，但是不要以这种方式去过去文件的大小，因为 C 语言标准并没有保证这一点。
    PRINT_LONG(ftell(file));

    fclose(file);

    PRINT_LONG(ftell(stdout));
    perror("ftell(stdout)");

    return 0;
}
