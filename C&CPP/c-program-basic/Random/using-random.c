#include <stdio.h>
#include <stdlib.h>
#include <time.h>

extern void srand1(unsigned int x);

extern int rand1(void);

static void randomUsingLib();

static void randomStdLib();

int main(void) {
    //randomUsingLib();
    randomStdLib();
    return 0;
}


//https://blog.csdn.net/dadaguaishangjiang/article/details/78874567
static void randomStdLib() {
    int arr[10] = {0};
    int i;
    //调用time函数来获取随机数
    srand((unsigned) time(NULL));

    for (i = 0; i < 10; i++) {
        arr[i] = rand() % 100;
        printf("%d\n", arr[i]);
    }

}

static void randomUsingLib() {
    int count;
    unsigned seed;

    printf("Please enter your choice for seed.\n");

    while (scanf("%u", &seed) == 1) {
        /* reset seed */
        /* 重置种子*/
        srand1(seed);
        for (count = 0; count < 5; count++) {
            printf("%d\n", rand1());
        }
        printf("Please enter next seed (q to quit):\n");
    }

    printf("Done\n");
}
