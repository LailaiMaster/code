#include "CppLibrary.h"
#include <cstdio>

int add(int a, int b) {

#ifdef  __cplusplus
    printf("CppLibrary running in the cpp\n");
#endif

    return a + b;
}