/*
 ============================================================================
 
 Author      : Ztiany
 Description : 复合类型：引用和指针

 ============================================================================
 */

#include "cstdlib"
#include "iostream"

using namespace std;

//1：引用类型
static void reference() {
    int value_a = 323;
    int &ref_a = value_a;//ref_a指向value_a
    //int &ref_b; //报错，引用类型必需被初始化
    ref_a = 3;
    cout << "value_a = " << value_a << endl;
}

//2：指针类型
static void pointer() {
    int value_a = 32;
    int *pA = &value_a;
    void *pVoid = &value_a;
    int *&refPa = pA;//refPa是对指针pA的引用
    cout << "value_a address = " << pA << endl;

    const int *pB = &value_a;//pB是一个指向整型常量的指针，pB不能修改其地址所指向的值
    int *const pC = &value_a;//pC是一个指针常量，pC不能再指向其他int变量
}

//3：const与引用
static void cons_reference() {
    int value_a = 3;
    const int &ref_a1 = value_a;
    const int &ref_a2 = 3;
    const int &ref_a3 = ref_a1 * 3;

    const int value_b = 3;
    //int &ref_b = value_b; //编译错误，因为value_b是一个常量，所以必需使用引用也必须是一个常量
}

int main() {
    reference();
    pointer();
    return EXIT_SUCCESS;
}
