#include <stdio.h>
#include <io_utils.h>

//=============== 只读指针 ===============
int main() {
    //=============== 只普通指针 ===============
    int a;
    int b;
    int *p = &a;

    PRINT_HEX(p);
    PRINT_HEX(&a);
    PRINT_INT(sizeof(int *));
    PRINT_INT(*p);
    PRINT_INT(a);

    int **pp = &p;
    *p = 20;
    PRINT_INT(*p);
    PRINT_INT(a);

    //=============== 只读指针 ===============
    int *const cp = &a;//由右往左看，const 离 * 近就是指针常量，不能改变指针的指向。
    *cp = 2; // OK
    //cp = &b; ERROR

    int const *cp2 = &a;//由右往左看，const 离 类型(int) 近就是常量指针，不能通过指针修改指针指向的值。
    // *cp2 = 2; ERROR
    cp2 = &b; // OK

    int const *const ccp = &a;//最严格的指针，都不能改。
    // ccp = &b; ERROR
    //*ccp = 2; ERROR
    return 0;
}
