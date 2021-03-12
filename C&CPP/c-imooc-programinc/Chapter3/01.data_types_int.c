#include <stdio.h>
//基本类型的极值
#include <limits.h>

int main() {
    //=============== int/long 数据类型 ===============
    short short_int = 0;
    int i = 100;
    long long_int = 0;
    long long longlong_int = 0;
    unsigned int unsigned_int = 123;
    unsigned long unsigned_long = 111;

    //=============== 格式化输出 ===============
    /*
        d = decimal
        x = hex
        o = oct
        hd%: short decimal
        %d: decimal
        %ld: long decimal
        %lld: long long decimal
        %hu: unsigned short decimal
        \n: new line
        zu: prints size_t as unsigned decimal
        zx: prints size_t as hex
        zd: prints size_t as signed decimal
     */
    printf("short int %d\n", short_int);
    printf("int in oct: %o\n", i);
    size_t size_of_int = sizeof(int);
    printf("short int: %d\n", sizeof(short int));
    printf("int: %d\n", sizeof(int));
    printf("int: %zd\n", size_of_int);
    printf("long int: %d\n", sizeof(long int));
    printf("long long: %d\n", sizeof(long long int));

    //=============== 极值 ===============
    printf("max int %d, min %d\n", INT_MAX, INT_MIN);
    printf("max int %ld, min %ld\n", LONG_MAX, LONG_MIN);
    printf("unsigned max int %u, unsigned min %d\n", UINT_MAX, 0);
    return 0;
}
