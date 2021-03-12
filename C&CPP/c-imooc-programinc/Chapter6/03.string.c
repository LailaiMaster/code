#include <stdio.h>
#include "io_utils.h"

//=============== 字符串 ===============
int main() {
    //=============== 字符串定义 ===============
    //char string[11] = "Hello World"; 会引发错误，因为还有一个 NULL \0 字符。
    //char string[12] = "Hello World";//可以，但是没必要。
    char string[12] = "Hello World";//编译期自己推断。
    for (int i = 0; i < 11; ++i) {
        PRINT_CHAR(string[i]);
    }
    PRINTLNF("%s", string);

    //=============== 中文字符串 ===============
    char string_zh[] = "你好，中国";//与文件编码有关，比如文件是 UTF-8 编码的，那么存的就是中文的 UTF-8 编码，一个 UTF-8 编码要多个字符，因此这里这个数组长度为 16.
    wchar_t ws[] = L"你好，中国";//存的都是码点，一个宽字符可以存储一个码点，数组长度为 6【可以通过断点调试查看内存信息】
    return 0;
}
