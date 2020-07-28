/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */
#include <inttypes.h>
#include <stdio.h>
#include<stdlib.h>

void writeInt32(int32_t value) {
    char s[40];//要转换成的字符数组

    while (1) {
        if (value <= 0x7f) {
            printf("%s\n", itoa(value, s, 2));
            break;
        } else {
            // 取低7位，再最高位赋1
            printf("%s\n", itoa(value & 0x7f | 0x80, s, 2));
            value >>= 7;
        }
    }
}


void writeInt64(int64_t value) {
    char s[100];//要转换成的字符数组
    uint64_t i = value;

    //0x7f: 0111 111
    //~0x7f: 1000 0000
    //1111 1111  1111 1111  1111 1111  1110 1100
    //                                       1000 0000

    while (1) {
        printf("i = %s\n", itoa(i, s, 2));
        printf("&& = %s\n", itoa((i & ~0x7f), s, 2));

        if ((i & ~0x7f) == 0) {
            printf("end = %s\n", itoa(i, s, 2));
            break;
        } else {
            // 取低7位，再最高位赋1
            printf("con = %s\n", itoa(i & 0x7f | 0x80, s, 2));
            i >>= 7;
        }
    }
}


int main() {
    writeInt32(9999999);
    printf("\n");
    writeInt64(-20);
}

