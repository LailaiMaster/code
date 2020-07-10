/*
 ============================================================================

 Author      : Ztiany
 Description : C++函数声明

 ============================================================================
 */


#include <iostream>
#include <vector>
#include <array>

using namespace std;

//1：引用形参，通过传递引用形参，可以改变实参的值
static void referenceParameter1(int &i) {
    //我们改变了i的值，利用这个特性，可以把参数用作返回额外从信息的载体
    i = 4;
}

//2：把参数声明为引用类型，可以避免是实参被拷贝，这里只会传递实参的引用
static bool referenceParameter2(const string &s1, const string &s2) {
    return s1.size() > s2.size();
}

//3：const形参：如果参数不需要被修改，应该声明为const的
static void constParameter1(const char *cp) {
    if (cp) {
        while (*cp) {
            cout << *cp << ", ";
            cp++;
        }
    }
    cout << endl;
}

//使用普通引用而非常量引用极大的限制了函数能接受的实参类型，这个函数只能接受非常量类型
static void constParameter2(char *cp) {
    if (cp) {
        while (*cp) {
            cout << *cp << ", ";
            cp++;
        }
    }
    cout << endl;
}

//4：数组传参，这里的arr[10]只表示期望给一个长度为10 的数据，但实际长度却不确定
static void arrayParameter1(int *arr) {
    cout << "array_parameter " << arr << endl;
}

//标准做法，传入头和尾，遍历数组
static void arrayParameter2(const int *begin, const int *end) {
    while (begin != end) {
        begin++;
    }
}

//标准做法，传入数组和数组的长度
static void arrayParameter3(const int *arr, size_t size) {

}

int main(int argc, char *argv[]) {

    //0：获取main函数的参数
    cout << argc << endl;
    for (int i = 0; i < argc; ++i) {
        cout << argv[i] << endl;
    }

    //1 referenceParameter1
    int intA = 1;
    referenceParameter1(intA);
    cout << "intA =" << intA << endl;

    //3 常量形参
    char *str1 = "abcdefg";
    const char *str2 = "hijklmn";
    constParameter1(str1);
    constParameter1(str2);
    constParameter2(str1);
    //constParameter2(str2);//常量类型传递给非产量类型时，类型不匹配，Parameter type mismatch: Assigning 'const char *' to 'char *' discards const qualifier

    //4 array_parameter
    int array[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    arrayParameter1(array);
}