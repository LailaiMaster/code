/*
 ============================================================================
 
 Author      : Ztiany
 Description : constexpr_type

 ============================================================================
 */

//constexpr常量表达式
static constexpr int size_a() {
    return 333;
}

static void constexpr_type() {
    constexpr int value_b = 3243;
    constexpr int value_c = value_b + 3;
    constexpr int sz = size_a();//只有size_a是一个constexpr函数时才是一条正确的声明语句
}