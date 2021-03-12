#include <io_utils.h>
#include <string.h>
#include <stdlib.h>

//=============== 常见内存操作函数 ===============
int main() {
    //=============== 内存拷贝 ===============
    //memcpy(); //需要传递 size 参数，否则不知道要复制多少。
    //strcpy();//不需要传递 size 参数，string 有结束标识符。

    //=============== 字符串查找 ===============
    // memchr()
    //  strchr()

    //=============== 字符串对比 ===============
    //  memcmp()
    //  strcmp()

    //=============== 内存设置：memset ===============
    char *mem = malloc(10);
    memset(mem, 0, 10);
    PRINT_INT_ARRAY(mem, 10);
    free(mem);

    //=============== 内存拷贝：memmove ===============
    //memmove 与 memcopy 功能完全相同，但是 memmove 允许 src 和 dest 覆盖。
    char src[] = "HelloWorld";
    char *dest = malloc(11);
    memset(dest, 0, 11);
    memcpy(dest, src, 11);
    puts(dest);
    memcpy(dest + 3, dest + 1, 4);//更推荐：memmove
    puts(dest);
    free(dest);
    return 0;
}
