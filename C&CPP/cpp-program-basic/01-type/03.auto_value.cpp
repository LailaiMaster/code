/*
 ============================================================================
 
 Author      : Ztiany
 Description : auto value

 ============================================================================
 */

//使用auto定义变量
static void autoValue() {
    int value_a = 3, value_b = 4;
    auto value_c = value_a + value_b;//编译器推断出value_c的类型是int类型
    auto pA = &value_a;//编译器推断出value_c的类型是int类型指针
    int *pB = pA;
    int value_d = value_c;
}