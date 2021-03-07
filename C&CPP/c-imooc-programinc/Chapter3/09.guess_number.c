#include <stdio.h>
#include <stdlib.h>
#include <time.h>

//=============== 练习：猜数字游戏 ===============
int main() {
    // 1. random number
    // [0, 0x7fff]
    srand(time(NULL));//随机数需要一个种子。
    int magic_number = rand();//然后得到一个随机数。

    // 2. loop
    while (1) {
        int user_input;
        char user_input_buffer[20];

        puts("Please input a number: ");
        fflush(stdin);
        fgets(user_input_buffer, 20, stdin);
        user_input = strtol(user_input_buffer, NULL, 10);
        //scanf("%d", &user_input);

        if (user_input > magic_number) {
            puts("Your number is bigger!");
        } else if (user_input < magic_number) {
            puts("Your number is smaller!");
        } else {
            puts("Yes! You got it!");
            break;
        }
    }

    return 0;
}
