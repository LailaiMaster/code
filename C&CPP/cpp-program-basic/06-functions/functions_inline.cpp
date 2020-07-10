/*
 ============================================================================
 
 Author      : Ztiany
 Description : 内联函数和 constexpr 函数

 ============================================================================
 */

/*声明内联函数*/
inline int add(int a, int b) {
    return a + b;
}

/*声明constexpr函数 */
constexpr int length() {
    return 3;
}