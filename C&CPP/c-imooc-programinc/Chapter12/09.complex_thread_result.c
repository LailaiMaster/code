#include <io_utils.h>
#include <tinycthread.h>
#include <stdlib.h>

typedef struct {
    int a;
    char *b;
} ComplexResult;

typedef struct {
    int arg;
    ComplexResult result;
} ComplexArg;

int ComplexReturnValue(ComplexArg *arg) {
    ComplexResult *complex_result = &arg->result;
    complex_result->a = 2;
    complex_result->b = "HelloWorld";
    return 0;
}

//=============== 处理复杂的线程返回的结果 ===============
/*
 * 子线程要返回复杂的结果时，如何处理【thrd_join 函数只接收 int 类型的指针】？
 * 1. 子线程中 malloc 一块内存，强转成 int* 返回，调用线程拿到 int* 后强转回来约定好的类型。
 *      【不可取，内存不好管理，指针与 int* 并不支持相转，某些情况下可以是因为 int 和指针占用内存正好相同，比如 32 位环境】
 * 2. 调用线程分配内存传给子线程去操作【参考下面方式】。
 * 3. 回调。【参考 10.callback】
 */
int main(void) {
    ComplexArg complex_arg = {.arg = 1};
    thrd_t t;
    thrd_create(&t, ComplexReturnValue, &complex_arg);

    int result;
    thrd_join(t, &result);
    ComplexResult *complex_result = &complex_arg.result;
    PRINT_INT(complex_result->a);
    puts(complex_result->b);
}