/*
 ============================================================================
 
 Author      : Ztiany
 Description : 字符串输入输出

 ============================================================================
 */

#include <cstdlib>
#include <string>
#include <iostream>

using namespace std;

void getStringByCin();

void getStringInWhile();

void getStringByGetLine();

int main() {
    //getStringByCin();
    //getStringInWhile();
    getStringByGetLine();
    return EXIT_SUCCESS;
}

/**
 * 使用getline读取整行文本
 */
void getStringByGetLine() {
    string result;
    getline(cin, result);
    cout << "result size = " << result.size() << endl;
    cout << "result = " << result << endl;
}

void getStringInWhile() {
    string result;
    //直接按回车，>> 不会返回。
    while (cin >> result) {
        if (result == "q") {
            break;
        }
        cout << "result size " << result.size() << endl;
        cout << result << endl;
    }
}

/*
 从标准输入读取 string 并将读入的串存储在 s 中。string 类型的输入操作符：
        •读取并忽略开头所有的空白字符（如空格，换行符，制表符）。
        •读取字符直至再次遇到空白字符，读取终止。
 */
void getStringByCin() {
    //输入：a b c
    string receive1;
    string receive2;
    string receive3;
    cin >> receive1;//a
    cin >> receive2;//b
    cin >> receive3;//c
    //输出：input is abc
    cout << "input is " << receive1 << receive2 << receive3 << endl;

    //输入和输出操作的行为与内置类型操作符基本类似。尤其是，这些操作符返回左操作数作为运算结果。因此，我们可以把多个读操作或多个写操作放在一起
    cin >> receive1 >> receive2 >> receive3;//c
    cout << "input is " << receive1 << receive2 << receive3 << endl;
}
