#include <stdio.h>
#include <io_utils.h>

int *pointer_at_large;

void DangerousPointer() {
    int a = 2;
    pointer_at_large = &a;
    // ...
    pointer_at_large = NULL;
}

//=============== 指针变量 ===============
int main() {
//  int *p = (int *)0x65fe18; // DANGEROUS !!!，很危险。
//  PRINT_INT(p);
//  PRINT_HEX(&p);
//  PRINT_INT(*p);

    int *p = NULL;//正确的初始化方式，没有指向任何内存
    //*p = 3; // error!!

    if (p) {//NULL = false

    }

    DangerousPointer();

    if (pointer_at_large) {
        PRINT_INT(*pointer_at_large);
    }

    return 0;
}