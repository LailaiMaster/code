#pragma once

#include <stdarg.h>

/**
 * usage:
 * char str[100];
 * c_sprintf(str, number1 = %, number2 = %d", 0, 888, -1000);
 *
 * @param buffer
 * @param fmt
 * @param ...
 */
void c_sprintf(char *buffer, const char *fmt, ...) {
    //声明va_list
    va_list arg_list;
    va_start(arg_list, fmt);
    char *b = buffer;
    //
    int count = 0;
    // *fmt != '\0'
    while (*fmt) {
        if (*fmt != '%') {
            count++;
            // fmt D的地址
            *b++ = *fmt++;
            continue;
        }
        //获得 % 后面的 d
        fmt++;
        switch (*fmt) {
            case 'd': {
                int i = va_arg(arg_list, int);
                int j = 0;
                char tmp[10];
                int sign = i < 0 ? 1 : 0;
                do {
                    //i = 888
                    //取出最后一个数字
                    int r = i % 10;
                    r = r < 0 ? -r : r;
                    //去掉最后一个数字
                    //记录
                    tmp[j++] = r + '0';
                } while (i /= 10);
                //tmp =  888
                // i= -123 tmp = 321-
                if (sign) {
                    tmp[j++] = '-';
                }
                while (j > 0) {
                    char a = tmp[--j];
                    *b++ = a;
                    count++;
                }

            }
                break;
            default:
                printf("xxxxxxx\n");
                break;
        }
        fmt++;
    }
    buffer[count] = '\0';
}
