#include <stdio.h>
#include "io_utils.h"
#include <stdlib.h>
#include <time.h>

#define PLAYER_COUNT 50

void SwapElements(int array[], int first, int second) {
    int temp = array[first];
    array[first] = array[second];
    array[second] = temp;
}

void ShuffleArray(int array[], int length) {
    //随机数种子
    srand(time(NULL));

    //[0, RAND_MAX]
    for (int i = length - 1; i > 0; --i) {
        //获取一个随机数
        int random_number = rand() % i;
        SwapElements(array, i, random_number);
    }
}

//=============== 案例：打乱数组顺序 ===============
int main() {
    int players[PLAYER_COUNT];
    for (int i = 0; i < 50; ++i) {
        players[i] = i;
    }
    // players : 0, 1, ..., 49
    PRINT_INT_ARRAY(players, PLAYER_COUNT);
    ShuffleArray(players, PLAYER_COUNT);
    PRINT_INT_ARRAY(players, PLAYER_COUNT);
    return 0;
}
