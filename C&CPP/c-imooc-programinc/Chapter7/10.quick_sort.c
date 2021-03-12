#include <stdio.h>
#include "io_utils.h"
#include <stdlib.h>
#include <time.h>

void SwapInt(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

void Shuffle(int *array, int length) {
    srand(time(NULL));
    for (int i = length - 1; i > 0; --i) {
        int random_number = rand() % i;
        SwapInt(array + i, array + random_number);
    }
}

int *Partition(int *low, int *high) {
    int pivot = *(low + (high - low) / 2);//切割数组时，选中间那个数
    int *p = low;
    int *q = high;

    while (1) {
        while (*p < pivot) p++;
        while (*q > pivot) q--;

        if (p >= q) break;
        SwapInt(p, q);
    }

    return q;
}

void QuickSort(int *low, int *high) {
    if (low >= high) return;
    int *partition = Partition(low, high);
    QuickSort(low, partition - 1);
    QuickSort(partition + 1, high);
}

#define PLAYER_COUNT 50

//=============== 案例：快排的另一个版本【Hoare partition scheme】  ===============
int main() {
    int *players = malloc(sizeof(int) * PLAYER_COUNT);
    if (!players) {
        return 1;
    }
    for (int i = 0; i < PLAYER_COUNT; ++i) {
        players[i] = i;
    }
    Shuffle(players, PLAYER_COUNT);
    PRINT_INT_ARRAY(players, PLAYER_COUNT);
    QuickSort(players, players + PLAYER_COUNT - 1);
    PRINT_INT_ARRAY(players, PLAYER_COUNT);
    free(players);

    return 0;
}
