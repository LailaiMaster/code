#include <stdio.h>
#include <stdbool.h>

void boolean_value() {
    //=============== 布尔值 ===============
    // _Bool, bool：c90 引入，bool 就是 _Bool 的别名。
    // true: 1, false: 0
    _Bool is_enabled = true;
    is_enabled = 10;
    printf("is_enabled: %d\n", is_enabled);
    is_enabled = false;
    bool is_visible = false;
}

void if_else() {
    //=============== if/else ===============
#define MAGIC_NUMBER 10
    int user_input;
    printf("Please input a number: \n");
    scanf("%d", &user_input);
    if (user_input > MAGIC_NUMBER) {
        printf("Your number is bigger!");
    } else if (user_input < MAGIC_NUMBER) {
        printf("Your number is smaller!");
    } else {
        printf("Yes! You got it!");
    }
}

void ternary_operator() {//=============== 三元运算符 ===============
    bool is_enabled = 10;
    bool is_visible = false;
    is_enabled = false;
    int is_open = is_enabled && is_visible ? 1 : 0;
    printf("is_open: %d\n", is_open);
}

void switch_condition() {
    //=============== switch ===============
#define ADD '+'
#define SUB '-'
#define MULTIPLY '*'
#define DIVIDE '/'
#define REM '%'

    int left;
    int right;

    char operator;
    printf("Please input an expression: \n");
    scanf("%d %c %d", &left, &operator, &right);

    int result;
    switch (operator) {
        case ADD:
            result = left + right;
            break;
        case SUB:
            result = left - right;
            break;
        case MULTIPLY:
            result = left * right;
            break;
        case DIVIDE:
            result = left / right;
            break;
        case REM:
            result = left % right;
            break;
        default:
            printf("Unsupported operation: %c\n", operator);
            return;
    }

    printf("Result: %d\n", result);
}

//=============== 条件语句 ===============
int main() {
    boolean_value();
    if_else();
    ternary_operator();
    switch_condition();
    return 0;
}
