#include <io_utils.h>
#include <stdlib.h>
#include <errno.h>
#include <time.h>
#include <string.h>
#include <stdbool.h>

void SwapString(char **first, char **second) {
    char *temp = *first;
    *first = *second;
    *second = temp;
}

void Shuffle(char **array, int length) {
    srand(time(NULL));

    for (int i = length - 1; i > 0; --i) {
        int random_number = rand() % i;
        SwapString(array + i, array + random_number);
    }
}

char **Partition(char **low, char **high) {
    char *pivot = *(low + (high - low) / 2);
    char **p = low;
    char **q = high;

    while (1) {
        while (strcmp(*p, pivot) < 0) p++;
        while (strcmp(*q, pivot) > 0) q--;

        if (p >= q) break;
        SwapString(p, q);
    }

    return q;
}

void QuickSort(char **low, char **high) {
    if (low >= high) return;
    char **partition = Partition(low, high);
    QuickSort(low, partition - 1);
    QuickSort(partition + 1, high);
}

//=============== 字符串的长度与字符串的比较 ===============
int main() {
    //=============== 字符串的长度 ===============
    char *string = "Hello World!";
    PRINT_INT(strlen(string));
    //PRINT_INT(strnlen_s(string, 100)); // C11, msvc【更安全的版本】
    //PRINT_INT(strnlen(string, 100)); // gcc【更安全的版本】

    //=============== 字符串的比较 ===============
    char *left = "Hello World!";
    char *right = "Hello C Programmers!";

    PRINT_INT(strcmp(left, right));
    PRINT_INT(strncmp(left, right, 5));//返回 0 表示一致

    //=============== 字符串的排序 ===============
    char *names[] = {
            "Cindy",
            "Don",
            "Andrey",
            "Elsa",
            "George",
            "Frank",
            "Benny",
    };

    QuickSort(names, names + 6);
    PRINT_ARRAY("%s, ", names, 7);
    return 0;
}
