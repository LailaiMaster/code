#include <io_utils.h>
#include <string.h>

//=============== 字符串的查找 ===============
int main() {
    //=============== 查单个字符 ===============
    char *string = "Hello World!";
    char *result = strchr(string, 'l');//正着查，返回的是匹配到的内存地址。
    char *result_reverse = strrchr(string, 'l');//反着查
    puts(result);
    puts(result_reverse);

    //=============== 在字符串中查字串 ===============
    char *substring_position = strstr(string, "Wor");//返回的是 World 的起始位置。
    printf("substring_position = %s\n", substring_position);
    PRINT_INT(substring_position - string);


    //=============== strpbrk 的使用 ===============
    /*
     * C 库函数 char *strpbrk(const char *str1, const char *str2) 检索字符串 str1 中第一个匹配字符串 str2 中字符的字符，不包含空结束字符。
     * 也就是说，依次检验字符串 str1 中的字符，当被检验字符在字符串 str2 中也包含时，则停止检验，并返回该字符位置。
     */
    char *string2 = "C, 1972; C++, 1983; Java, 1995; Rust, 2010; Kotlin, 2011";
    char *break_set = ",;";

    int count = 0;
    char *p = string2;

    do {
        p = strpbrk(p, break_set);//返回的值不为空，表示找到下一个位置。
        if (p) {
            puts(p);
            p++;
            count++;
        }
    } while (p);

    PRINTLNF("Found %d characters.", count);
    return 0;
}
