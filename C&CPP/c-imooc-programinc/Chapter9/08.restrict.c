#include <io_utils.h>
#include <string.h>
#include <stdlib.h>

//=============== C99 引入的 restrict ===============
int main() {
    /*
     * memcpy 的原型：void * __cdecl memcpy(void * __restrict__ _Dst,const void * __restrict__ _Src,size_t _Size)
     *  两个参数都加上了 __restrict__ 标识，标识这两个内存区域不允许有重叠。也即如果加上了 __restrict__，就保证这两块区别没有重叠。
     */
    char *mem = malloc(10);
    memset(mem, 0, 10);

    PRINT_INT_ARRAY(mem, 10);
    free(mem);

    char *left = "Hello World!";
    char *right = "Hello C Programmers!";

    PRINT_INT(strcmp(left, right));
    PRINT_INT(strncmp(left, right, 5));

    char src[] = "HelloWorld";
    char *dest = malloc(11);
    memset(dest, 0, 11);
    memcpy(dest, src, 11);
    puts(dest);
    memcpy(dest + 3, dest + 1, 4);
    puts(dest);
    free(dest);
    return 0;
}
