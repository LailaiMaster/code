#include <stdio.h>
#include <io_utils.h>

//=============== 指针基础 ===============
int main() {
    //=============== 取地址/取值 ===============
    int a;
    scanf("%d", &a);
    int *p = &a;

    PRINT_HEX(p);
    PRINT_HEX(&a);
    PRINT_INT(sizeof(int *));
    PRINT_INT(*p);
    PRINT_INT(a);

    //=============== 多级指针 ===============
    int **pp = &p;
    return 0;
}
