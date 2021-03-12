#include <io_utils.h>

int IsBigEndian() {
    union {
        char c[2];
        short s;
    } value = {.s=0x100};
    //0x100 --> 0001 0000 0000 栈空间向低位增长。【变量肯定是低到高存】
    //小端：0000 0000 0001。高位内存地址在后，地位地址在前。
    //大端，0001 00000000。高位内存地址在前，地位地址在后。
    return value.c[0] == 1;
}

int IsBigEndian2() {
    short s = 0x100;
    char *p = (char *) &s;
    return p[0] == 1;
}

int ToggleEndian(int original) {
    union {
        char c[4];
        int i;
    } value = {.i=original};

    char temp = value.c[0];
    value.c[0] = value.c[3];
    value.c[3] = temp;
    temp = value.c[1];
    value.c[1] = value.c[2];
    value.c[2] = temp;

    return value.i;
}

int ToggleEndian2(int original) {
    char *p = (char *) &original;
    char temp = p[0];
    p[0] = p[3];
    p[3] = temp;
    temp = p[1];
    p[1] = p[2];
    p[2] = temp;

    return original;
}

//=============== 案例：判断字节顺序【大端/小段】 ===============
int main() {
    PRINT_INT(IsBigEndian());
    int original = 0x12345678;
    PRINT_HEX(ToggleEndian(original));
    return 0;
}
