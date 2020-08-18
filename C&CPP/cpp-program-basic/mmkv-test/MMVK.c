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
            printf("write = %s\n", itoa(value, s, 2));
            break;
        } else {
            // 取低7位，再最高位赋1
            printf("write = %s\n", itoa(value & 0x7f | 0x80, s, 2));
            value >>= 7;
        }
    }
}

void writeInt32Way2(int32_t value) {
    char s[100];//要转换成的字符数组
    uint32_t i = value;
    while (1) {
        if ((i & ~0x7F) == 0) {
            printf("write = %s\n", itoa(i, s, 2));
            break;
        } else {
            // 取低7位，在最高位赋1
            printf("write = %s\n", itoa(i & 0x7F | 0x80, s, 2));
            i >>= 7;
        }
    }
}

void writeInt64(int64_t value) {
    char s[100];//要转换成的字符数组
    uint64_t i = value;
    //0x7f: 0111 111
    //~0x7f: 1000 0000
    //1111 1111  1111 1111  1111 1111  1110 1100
    while (1) {
        if ((i & ~0x7f) == 0) {
            printf("write = %s\n", itoa(i, s, 2));
            break;
        } else {
            // 取低7位，在最高位赋1
            printf("write = %s\n", itoa(i & 0x7f | 0x80, s, 2));
            i >>= 7;
        }
    }
}

int8_t readN1Byte() {
    static int times = 1;
    times++;
    if (times < 10) {
        return 255;
    } else {
        return 1;
    }
}

//0x80000000
int8_t readNMaxByte() {
    static int times = 1;
    times++;
    if (times <= 5) {
        return 128;
    } else if (times <= 6) {
        return 248;
    } else if (times <= 10) {
        return 255;
    }
    return 1;
}

void readInt32() {
    char s[100];//要转换成的字符数组

    int8_t tmp = readNMaxByte();
    printf("read = %s\n", itoa(tmp, s, 2));

    // 读出来是负数 则最高位必为1
    if (tmp >= 0) {
        printf("result = %d\n", tmp);
    }
    //获得低7位数据
    int32_t result = tmp & 0x7f;
    int32_t i = 1;
    while (1) {
        tmp = readNMaxByte();
        printf("read = %s\n", itoa(tmp, s, 2));

        if (i > 4) {
            break;
        }

        if (tmp >= 0) {
            //最高位就是0 不用 &7f 了
            result |= tmp << (7 * i);
            break;
        }
        // 当前字节放到前面 与 result 合并
        //最高位为1,只拿低7位
        result |= (tmp & 0x7f) << (7 * i);
        i++;
    }
    printf("read in b= %s\n", itoa(result, s, 2));
    printf("result = %d\n", result);
}

int main() {
    //writeInt32(9999999);
    printf("\n");
    printf("\n");
    printf("\n");
    writeInt64(-2147483648);
    printf("\n");
    writeInt32(0x80000000);
    printf("\n");
    readInt32();
}