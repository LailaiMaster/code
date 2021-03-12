#include <stdio.h>
#include <limits.h>

int main() {
    //=============== 字符集 ===============
    // 字符集 ASCII 127
    char a = 'a'; // 97，字符本质上也是整数
    char char_1 = '1';// 49
    char char_0 = '0';// 48
    char i = 0; // \0, NULL。特殊字符。

    //=============== 字面量(literal) & 特殊字符 ===============
    //特殊字符
    /*
         \n : newline
        \b : backspace
        \r : return
        \t : table
     */
    char newline = '\n';

    //字符字面量
    /*
         \' : ' 字符字面量
        \" : " 字符串字面量
     */
    char char_1_escape_oct = '\61';// "\"后面接的是八进制，八进制的 61 = 十进制的 49
    char char_1_escape_hex = '\x31';//x 表示十六进制，八进制的 31 = 十进制的 49

    //以不同的格式输出字符
    printf("char a: %d\n", a);
    printf("char 1: %d\n", char_1);
    printf("char 'i': %d\n", i);
    printf("char 1: %c\n", char_1);
    printf("char 1: %c\n", char_1_escape_oct);
    printf("char 1: %c\n", char_1_escape_hex);

    //=============== 字符集 & 宽字符 ===============
    //CJK 中日韩字符集
    // Unicode  统一字符集
    // Code point 码点
    // C95 开始支持宽字符
    wchar_t zhong = L'中'; //在内存中，宽字符用码点表示。
    wchar_t zhong_hex = L'\u4E2D';// u 表示 unicode，而 "4e2d" 就是“中”在字符集中的编号，也叫码点。
    printf("中：%d\n", zhong);
    printf("中：%d\n", zhong_hex);

    //=============== 字符串 ===============
    // 字符串
    char *string = "中";
    return 0;
}
