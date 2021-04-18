#include <cstdlib>
#include <cstdint>

/*
 ============================================================================
 
 Author      : Ztiany
 Description : 使用使用可移植的类型。

 ============================================================================
 */
int main() {
    int32_t int_a = 3;
    int32_t int_b = 3;

    int32_t *const pa = &int_a;
    int32_t const *pb = &int_a;
    pb = &int_b;

    return EXIT_SUCCESS;
}