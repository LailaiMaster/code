/*
 ============================================================================
 
 Author      : Ztiany
 Description : C 调用 CPP

 ============================================================================
 */
#include <stdlib.h>
#include <stdio.h>
#include "CppLibrary.h"

int main() {
#ifdef  __cplusplus
    printf("main.c running in the cpp\n");
#else
    printf("main.c running in the c\n");
#endif

    int a = 10;
    int b = 90;
    int ret = add(a, b);
    printf("c call cpp result = %d", ret);
    return EXIT_SUCCESS;
}