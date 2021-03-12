#include <stdio.h>
#include <io_utils.h>
#include <locale.h>
#include <wchar.h>

#define ERROR_ILLEGAL_FILENAME -1
#define ERROR_CANNOT_OPEN_FILE -2
#define ERROR_READ_FILE -3
#define ERROR_UNSUPPORTED_CHARSET -99

#define CHARSET_UTF8 0
#define CHARSET_GBK 1

int CountCharactersInFile(char const *filename, int charset) {
    if (!filename) return ERROR_ILLEGAL_FILENAME;

    FILE *file;

    switch (charset) {
        case CHARSET_GBK:
#ifdef _WIN32//windows
            setlocale(LC_ALL, "chs");
#else//linux
            setlocale(LC_ALL, "zh_CN.gbk");
#endif
            file = fopen(filename, "r");
            break;
        case CHARSET_UTF8:
            setlocale(LC_ALL, "zh_CN.utf-8");
#ifdef _WIN32
            file = fopen(filename, "r, ccs=utf-8");
#else
            file = fopen(filename, "r");
#endif
            break;
        default:
            return ERROR_UNSUPPORTED_CHARSET;
    }

    if (!file) return ERROR_CANNOT_OPEN_FILE;

#define BUFFER_SIZE 512
    wchar_t wcs[BUFFER_SIZE];
    int count = 0;
    while (fgetws(wcs, BUFFER_SIZE, file)) {
        count += wcslen(wcs);//wcslen 是 strlen 的宽字符版本
    }

    if (ferror(file)) {
        perror("CountCharactersInFile error");
        fclose(file);
        return ERROR_READ_FILE;
    }

    fclose(file);

    return count;
}

//=============== 统计文件字符格式 ===============
/*
 * 1. 窄字符本质上就是一个个字节，因此默认版本函数只支持 ASCII 编码。
 * 2. 使用宽字符版本的函数，以适用于所有编码。在 msvc 下宽字符是 short 类型，在 linux 下宽字符是 int 类型。
 */
int main() {
    PRINT_INT(CountCharactersInFile("data/sanguo_utf8.txt", CHARSET_UTF8));
    PRINT_INT(CountCharactersInFile("data/sanguo_gbk.txt", CHARSET_GBK));
    return 0;
}
