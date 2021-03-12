/*
 ============================================================================

 Author      : Ztiany
 Description : CPP 调用 C【情况1：c 头文件加上 extern "C"】

 ============================================================================
 */

#include <iostream>
#include "CLibrary.h"

int main() {
#ifdef  __cplusplus
    printf("running in the cpp\n");
#else
    printf("running in the c\n");
#endif

    int a = 100;
    int b = 300;
    int ret = add(a, b);
    std::cout << "cpp call c add result = " << ret << std::endl;
    return 0;
}