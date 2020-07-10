/*
 ============================================================================
 
 Author      : Ztiany
 Description : 默认参数

 ============================================================================
 */

#include <cstdlib>
#include <string>
#include <iostream>

using namespace std;

typedef string::size_type sz;

/*参数中可以定义默认参数*/
static string screen(sz ht = 24, sz wid = 80, char back = ' ');

int main() {
    string window;

    window = screen();
    window = screen(33);
    window = screen(33, 44);
    window = screen(33, 44, '#');

    sz local = 55;
    //window(locale)//局部变量不能作为默认实参

    return EXIT_SUCCESS;
}

static string screen(sz ht, sz wid, char back) {
    cout << "----------------------------------------------" << endl;
    cout << "ht = " << ht << endl;
    cout << "wid = " << wid << endl;
    cout << "back = " << back << endl;
    return "Fake Screen";
}