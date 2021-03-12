#include <stdio.h>
//用于处理变长参数
#include <stdarg.h>

/**开发者使用变长参数的方式*/
void HandleVarargs(
        int arg_count,//边长参数的个数
        ...// 参数
) {
    // 1. 定义 va_list 用于获取我们变长参数
    va_list args;

    // 2. 开始遍历
    va_start(args, arg_count);
    for (int i = 0; i < arg_count; ++i) {
        // 3. 取出对应参数，(va_list, type)
        int arg = va_arg(args, int);
        printf("%d: %d\n", i, arg);
    }

    // 4. 结束遍历
    va_end(args);
}

//=============== 变长参数 ===============
int main(void) {
    printf("Hello World\n");
    HandleVarargs(4, 1, 2, 3.0, 4);
    return 0;
}