#include <stdio.h>
#include <stdlib.h>
#include <io_utils.h>

#define PLAYER_COUNT 10

void InitPointer(int **ptr, int length, int default_value) {
    *ptr = malloc(sizeof(int) * length);
    for (int i = 0; i < length; ++i) {
        (*ptr)[i] = default_value;
    }
}

//=============== 动态内存分配 ===============
int main() {
    int *players;
    //malloc 分配的内存是没有被初始化的，值未知
    //InitPointer(&players, PLAYER_COUNT, 0);
    //calloc 分配的内存是初始化过的
    players = calloc(PLAYER_COUNT, sizeof(int));

    for (int i = 0; i < PLAYER_COUNT; ++i) {
        PRINT_INT(players[i]);
        players[i] = i;
    }
    PRINT_INT_ARRAY(players, PLAYER_COUNT);

    //重新分配空间。能追加就追加，否则就清空然后重新分配。【重新分配的新空间也不会被初始化，只有 calloc 会初始化。】
    players = realloc(players, PLAYER_COUNT * 2 * sizeof(int));
    PRINT_INT_ARRAY(players, PLAYER_COUNT * 2);

    //释放内存
    if (players) {
        free(players);
    } else {

    }

    //=============== 常见指针使用错误 ===============
    /*
     * 1. 忘记使用完后释放。
     * 2. 使用了已经释放的内存。
     * 3. 使用超出编解的内存。
     * 4. 改变了内存的指针，导致无法正常释放
     */
    //=============== 动态内存使用建议 ===============
    /*
     * 1. 避免修改指向已分配内存的指针。
     * 2. 对于 free 后的指针主动位置 NULL。
     * 3. 避免将过多的指针指向动态分配的内存。
     * 4. 动态内存遵循谁分配谁释放原则。
     */

    return 0;
}