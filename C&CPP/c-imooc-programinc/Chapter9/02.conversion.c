#include <io_utils.h>
#include <stdlib.h>
#include <errno.h>

//=============== 字符串与其他数据类型的转换 ===============
/*
 * atox：使用简单，适用于简单，要求不高的场景。
 * strtox：可重复解析，更安全，功能更强大。
 */
int main() {
    //=============== atox 类 ===============
    PRINT_INT(atoi("1234")); // 1234
    PRINT_INT(atoi("-1234")); // -1234
    PRINT_INT(atoi("   1234abcd")); // 1234
    PRINT_INT(atoi("0x10")); // 0

    PRINT_DOUBLE(atof("12.34")); // 12.34
    PRINT_DOUBLE(atof("-12e34")); // -1.2e+35
    PRINT_DOUBLE(atof("   1.234abcd")); // 1.234
    PRINT_DOUBLE(atof("0x10")); // 16【mingw 不支持】
    PRINT_DOUBLE(atof("0x10p3.9")); // 128，科学计数法，用 p 表示，2 的 3 次方，.9 会被忽略。 16 * 2^3 = 128【mingw 不支持】

    //=============== strtox 类 ===============
    char const *const kInput = "1 200000000000000000000000000000 3 -4 5abcd bye";
    PRINTLNF("Parse: %s", kInput);

    char const *start = kInput;
    char *end;//剩余未解析的字符串

    while (1) {
        errno = 0;

        const long i = strtol(start, &end, 10);

        if (start == end) {
            break;
        }

        //使用 %.*s，则可以传递 (int) (end - start), start，表示打印从指定位置开始 start 内容。
        printf("'%.*s'\t ==> %ld.", (int) (end - start), start, i);

        /* ERANGE 表示溢出，可能解析的数字超出数据类型可承载范围。*/
        if (errno == ERANGE) {
            perror("");
        }

        putchar('\n');
        start = end;
    }

    PRINTLNF("Left: %s", end);
    return 0;
}
