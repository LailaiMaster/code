/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */
#include <iso646.h>
#include <stdlib.h>
#include <stdio.h>

int main() {

    int a;
    int result = scanf("input a number: %d", &a);

    if (result >= 0 and a > 10) {
        printf("Very Good");
    }
    return 0;
}

