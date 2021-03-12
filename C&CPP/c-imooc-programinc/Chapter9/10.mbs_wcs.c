#include <io_utils.h>
#include <string.h>
#include <stdlib.h>
#include <wchar.h>
#include <wctype.h>
#include <locale.h>

//=============== 宽字符与窄字符的转换 ===============
int main() {

    //=============== 针对宽字符，都有对应版本的函数支持 ===============
    // support for wide string
    {
        PRINT_BOOL(iswalpha(L'A'));
        PRINT_BOOL(iswdigit(L'2'));

        /*
         * 函数运行结果错误，比如下面 "PRINT_INT(length); " 语句打印结果为 9（明显是错误的，因为只有 8 个字符），如何解决？
         *  1. 原因：源代码文件的编码与编译器使用的编码不一致，导致解析  L"你好 Hello" 时发生错误，宽字符存的是码点，而不是编码。
         *  2. 方案：告诉 CMake 我们使用的什么编码：如果是 MSVC（默认为 GBK），在 Build, Execution, Deployment --> CMake --> CMake options 中输入 -DCMAKE_C_FLAGS='/utf-8'，也可以在 CMakeLists.text 中指定。
         */
        wchar_t *wcs = L"你好 Hello";
        size_t length = wcslen(wcs);
        PRINT_INT(length);

        wchar_t src[] = L"HelloWorld";
        wchar_t *dest = malloc(sizeof(wchar_t) * 11);
        wmemset(dest, 0, 11);
        wmemcpy(dest, src, 11);
        _putws(dest);
        wmemcpy(dest + 3, dest + 1, 4);
        _putws(dest);
        free(dest);
    }

    //=============== 转换宽字符与窄字符（conversions） ===============
    /*
     * 命名方式：
     *  mb：multibytes，多字节，对应的就是窄字符。
     *  mbs：multibytes string，string，对应的就是窄字符串。
     *  wc：wide character，对应的就是宽字符。
     *  wcs：wide character string，对应的就是宽字符串。
     */

    /*
     * 转换后，打印时遇到乱码问题解决：指定 mbstowcs 函数转换过程中使用什么来编码，通过 setlocale 函数来指定。
     */
    char *new_locale = setlocale(LC_ALL, "zh_CN.utf8");
    if (new_locale) {
        puts(new_locale);
    }

    {
        char mbs[] = "你好";
        wchar_t wcs[10];
        mbstowcs(wcs, mbs, 10);//mbs 窄字符串 --> wcs 宽字符串
        wprintf(L"%s\n", wcs);
    }
    return 0;
}
