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
    srand(time(NULL));
    //[0, RAND_MAX]
    for (int i = length - 1; i > 0; --i) {
        int random_number = rand() % i;
        SwapElements(array, i, random_number);
    }
}

/**对数组进行切分*/
int Partition(int array[], int low, int high) {
    int pivot = array[high];//最后一个元素为中枢，拆分后，左边的元素比中枢小，右边的数组比中枢大。
    int partition = low;
    for (int i = low; i < high; ++i) {
        if (array[i] < pivot) {
            SwapElements(array, i, partition++);
        }
    }
    SwapElements(array, partition, high);
    return partition;
}

void QuickSort(int array[], int low, int high) {
    if (low >= high) return;
    int partition = Partition(array, low, high);
    QuickSort(array, low, partition - 1);
    QuickSort(array, partition + 1, high);
}

//=============== 数组的排序：快排【Lomuto partition scheme】 ===============
int main() {
    int players[PLAYER_COUNT];
    for (int i = 0; i < 50; ++i) {
        players[i] = i;
    }

    // 打乱数组
    PRINT_INT_ARRAY(players, PLAYER_COUNT);
    ShuffleArray(players, PLAYER_COUNT);
    PRINT_INT_ARRAY(players, PLAYER_COUNT);

    //进行快排
    QuickSort(players, 0, PLAYER_COUNT - 1);

    PRINT_INT_ARRAY(players, PLAYER_COUNT);

    return 0;
}
