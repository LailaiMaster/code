#include <stdio.h>
#include <limits.h>

int main() {
    //=============== 变量 ===============
    // <type> <name>;
    int value;
    // <type> <name> = <initialized value>
    int value_init = 3;
    value = 4;
    value_init = 5;
    printf("value: %d\n", value);

    value_init = value;
    printf("size of value: %d\n", sizeof(value));
    //"%#x " 中的 # 用于表示用指定的进制输出，其中 x 的作用是在打印的内容前加进制标识，比如十六进制位 0x。
    //# indcates an alternative format. For x this means that 0x is prepended to the output.
    // 参考：
    //      <https://stackoverflow.com/questions/3529777/do-you-know-about-x-in-c-language-format-string>
    //      <http://www.cplusplus.com/reference/cstdio/printf/>
    printf("address of value: %#x\n", &value);

    //=============== 变量命名 ===============
    // key words 标识符 identifier
    // 1. a-zA-Z0-9_
    // 2. 数不能在第一个
    // 3*. Google code style, a-z_a-z，person_name
    float a_float3 = 3.14f;
    float a_float = 3.14f;
    return 0;
}
