/*
 ============================================================================
 
 Author      : Ztiany
 Description : decltype_type

 ============================================================================
 */

//使用delctype定义变量
static void decltype_type() {
    const int ci = 0, &cj = ci;
    decltype(ci) x = 0;//x的类型是const int
    decltype(cj) y = x;//y的类型是const int&，y绑定到了x
    //decltype(cj) z; //decltype(cj)是引用类型，必需初始化

    int i = 4, h = 44, *p = &i, &r = i;
    decltype(r + 0) b;//加法的结果是一个int，所以b的类型是一个int类型
    //(*p)是引用类型int&，必需被初始化。如果表达式的内容是解引用操作，则decltype将得到引用类型，
    // 正如我们熟悉的那样，解引用指针可以得到指针所指的对象，而且还能给这个对象赋值，因此decltype(*p)的结果就是int&，而非int
    decltype(*p) c = i;
    decltype(i) j;
    // decltype((i))形式的结果永远是引用，而 decltype(i)只有当i本身是一个引用的时候才是引用。
    decltype((i)) rB = h;
}