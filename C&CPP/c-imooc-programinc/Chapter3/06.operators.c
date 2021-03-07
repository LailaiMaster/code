#include <stdio.h>

int main() {
    //=============== 表达式 ===============
    int first = 0;
    int second;
    int third;

    third = second = first;

    int left, right;
    left = 2;
    right = 3;

    //=============== 四则运算符 ===============
    int sum;
    sum = left + right; // 5
    int diff = left - right; // -1
    int product = left * right; // 6
    int quotient = left / right; // 0
    float quotient_float = left / right; // 0
    float quotient_float_correct = left * 1.f / right; // 0.666666....
    int remainder = left % right; // 2
    int quotient_1 = 100 / 30; // 3

    printf("sum: %d\n", sum);
    printf("diff: %d\n", diff);
    printf("product: %d\n", product);
    printf("quotient: %d\n", quotient);
    printf("quotient_1: %d\n", quotient_1);
    printf("quotient_float: %f\n", quotient_float);
    printf("quotient_float_correct: %f\n", quotient_float_correct);
    printf("remainder: %d\n", remainder);

    //=============== 关系运算符 ===============
    // > < >= <= == !=
    // true: 1, false: 0
    printf("3 > 2: %d\n", 3 > 2);
    printf("3 < 2: %d\n", 3 < 2);
    printf("3 <= 3: %d\n", 3 <= 3);
    printf("3 >= 3: %d\n", 3 >= 3);
    printf("3 == 3: %d\n", 3 == 3);
    printf("3 != 3: %d\n", 3 != 3);

    //=============== 逻辑运算符 ===============
    // && 与 || 或
    printf("3 > 2 && 3 < 2: %d\n", 3 > 2 && 3 < 2);
    printf("3 > 2 || 3 < 2: %d\n", 3 > 2 || 3 < 2);

    //=============== 自增的运算顺序 ===============
    // ++ --
    int i = 1;
    int j = i++; // j = 1, i = 2;
    int k = ++i; // k = 3, i = 3;

    printf("i: %d\n", i);
    printf("j: %d\n", j);
    printf("k: %d\n", k);

    //=============== 位运算符【与/或/非】 ===============
    // bit operators & | ^ ~
#define FLAG_VISIBLE 0x1 // 2^0, 0001
#define FLAG_TRANSPARENT 0x2 // 2^1, 0010
#define FLAG_RESIZABLE 0x4 // 2^2, 0100
    int window_flags = FLAG_RESIZABLE | FLAG_TRANSPARENT; // 0110
    int resizable = window_flags & FLAG_RESIZABLE; // 0100
    int visible = window_flags & FLAG_VISIBLE; // 0000

    //=============== 位运算符【左移/右移】 ===============
#define FLAG_VISIBLE 1 << 0 // 2^0, 0001
#define FLAG_TRANSPARENT 1 << 1// 2^1, 0010
#define FLAG_RESIZABLE 1 << 2 // 2^2, 0100

    int x = 1000;
    x * 2;
    x << 1;

    x / 2;
    x >> 1;

    x *= 2; // x = x * 2;
    x /= 2; // x = x / 2;
    x += 2;
    x -= 2;
    x %= 2;

    x >>= 1;
    x <<= 1;

    //=============== , 号运算符【不推荐】 ===============
    int y;
    y = x = x * 2, x = x + 3; // warning
    return 0;
}
