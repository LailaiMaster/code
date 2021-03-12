#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <io_utils.h>

//函数版本：适用于 int
void SwapInt(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

//函数版本：适用于任何类型
void Swap(void *first, void *second, size_t size) {
    void *temp = malloc(size);
    if (temp) {
        memcpy(temp, first, size);
        memcpy(first, second, size);
        memcpy(second, temp, size);
        free(temp);
    } else {
        // ...
    }
}

/**
 * 宏函数版本：优势，没有类型概念。
 *      1. 为了防止宏展开后，temp 变量和所在区域冲突，要加上大括号： { type temp = a; a = b; b = temp; }。
 *      2. 加上大括号后，如果是   if (a) SWAP(a, b, int);，那么展开后就是  if (a){ ... };，由于有了 ; 结束了 if 语义，导致原本后面的 else 失效，无法编译，因此改成 do{...}while(0) 的形式。
 */
#define SWAP(a, b, type) do { type temp = a; a = b; b = temp; }while (0)
/**
 * 不需要传递类型的版本：typeof 会在编译时直接替换为真实的类型，typeof 不是 c 标准，不具备可移植性。
 *      在 msvc 中没有 typeof，如果是用 C++ 编程，则可使用 decltype(a)。
 *      SWAP_EXT(x, y); // gcc OK; msvc ERROR
 *      typeof(a) number = 0; // gcc OK; msvc ERROR
 */
#define SWAP_EXT(a, b) do { typeof(a) temp = a; a = b; b = temp; }while (0)

//=============== 案例：通过指针交换两个变量的值 ===============
int main() {
    int a = 0;
    int b = 1;

    double x = 3.0;
    double y = 4.0;

    // SwapInt
    SwapInt(&a, &b);
    Swap(&x, &y, sizeof(double));

    if (a) SWAP(a, b, int);
    else {
        puts("no swap.");
    }

    PRINT_INT(a);
    PRINT_INT(b);
    PRINT_DOUBLE(x);
    PRINT_DOUBLE(y);
    return 0;
}
