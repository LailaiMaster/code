#include <stdio.h>
#include <io_utils.h>
#include <stddef.h>

//修改对齐位数【全局设置】
//#pragma pack(2)

//=============== 内存对齐【方便 CPU 快速访问】 ===============
int main() {
    //=============== 不合理的内存布局 ===============
    typedef struct {
        char a; // 1
        char b; // 1
        //=============== 为属性单独配置对齐位数 ===============
        // __attribute((aligned(2))) int c; // 4 gcc only【gcc 支持的为属性配置对齐位数的功能】
        //_Alignas(8) int c; // 4 c11【g11 添加了 _Alignas，功能与 __attribute 类似，要求是不能比数据类型的位数小，比如 int，不能少于 4。】
        int c;
        short d; // 2
        double e; // 8
    } Align;

    //=============== 合理的内存布局 ===============
    typedef struct {
        char a; // 1
        char b; // 1
        short d; // 2
        int c; // 4
        double e; // 8
    } OptimizedAlign;

    /*
        61 62 00 00   03 00 00 00   04 00 00 00   00 00 00 00
        00 00 00 00   00 00 14 40
     */
    Align align = {'a', 'b', 3, 4, 5.0};
    /*
     61 62 01 00   02 00 00 00   00 00 00 00   00 00 14 40
     */
    OptimizedAlign optimizedAlign = {'a', 'b', 1, 2, 5.0};

    //=============== 查看变量对齐的位数 ===============
    PRINT_INT(_Alignof(align.c));//C11 Only
    PRINT_INT(offsetof(Align, e));//参数：(类型, 属性名)
    return 0;
}
