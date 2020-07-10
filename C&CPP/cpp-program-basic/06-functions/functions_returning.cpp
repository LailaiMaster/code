/*
 ============================================================================
 
 Author      : Ztiany
 Description : C++函数返回类型

 ============================================================================
 */
#include <cstdlib>
#include <string>
#include <vector>

using namespace std;

//1：返回值，拷贝值
const static string shortString(const string &s1, const string &s2) {
    return s1.size() > s2.size() ? s2 : s1;
}

//2：返回引用，不拷贝值
const static string &shortString(const string s1, const string s2) {
    return s1.size() > s2.size() ? s2 : s1;
}

//3：返回列表
static vector<string> getMessage() {
    return {"A", "B", "C"};//列表转换为vector
}

//4：严重错误：返回局部变量的引用
static string &getString() {
    string ret;
    if (ret.empty()) {
        ret = "empty";
    }
    return ret;
}

//5：返回数组指针
typedef int arrI[10];
//或者使用using，这等价于上面声明
using arrI = int[10];

static arrI *getIntegers();//如果没有用到函数声明，该函数可以没有定义

static arrI *getIntegers() {
    int (*pInt)[10] = new int[1][10];
    return pInt;
}

static int (*returnArray(int i))[10] {
    int (*pInt)[10] = new int[1][10];
    return pInt;
}

//5：返回数组指针
void returningArrPointer() {
    int (*pInt)[10] = getIntegers();
    delete[](pInt);
}

//6：C++ 11规定，函数可以返回花括号包围的值的列表。返回的列表用于对函数返回的临时量做初始化：
static vector<int> returnBracket() {
    return {1, 2, 3, 4};
}

int main() {
    returningArrPointer();
    return EXIT_SUCCESS;
}
