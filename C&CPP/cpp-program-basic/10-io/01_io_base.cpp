/*
 ============================================================================
 
 Author      : Ztiany
 Description : IO类

 ============================================================================
 */

#include <cstdlib>
#include <iostream>

void ioStatus();
void ioError();
void ioFlush();
void assessStatus();
void tieIO();

using namespace std;

int main() {
    //ioStatus();
    //ioError();
    //ioFlush();
    //assessStatus();
    tieIO();
    return EXIT_SUCCESS;
}

void tieIO() {
    cin.tie(&cerr);
    string receive;
    while (getline(cin, receive)) {
        if (receive == "q") {
            break;
        }
        cerr << receive << endl;
    }
}

//具体参考[关于《C++ Primer》中cin.fail可能造成死循环的问题分析](https://niyanchun.com/cin-fail-infinite-loop.html)。
void assessStatus() {
    int ival;//循环读取一个 int 值。
    while (cin >> ival, !cin.eof()) {
        if (cin.bad()) {
            //出现致命错误，程序需要退出
            throw runtime_error("IO stream corrupted");
        }
        cout << "is fail = " << cin.fail() << endl;// 警告用户
        if (cin.fail()) { // 错误的输入
            cerr << "bad data, try again" << endl;// 警告用户
            cin.clear(iostream::goodbit);//重置状态
            cin.ignore();//清除缓冲区，否则错误的输入一直在缓冲区中
            continue;//继续
        }
    }
}

void ioFlush() {
    cout << unitbuf;//每一次操作都要刷新一次
    cout << "hello";
    cout << nounitbuf;//复位
}

void ioError() {
    int word;
    while (cin >> word) {//输入不合法则条件为false
        //ok 操作是成功的
        cout << "word = " << word << endl;
    }
}

void ioStatus() {
    cout << "hello" << endl;
    cout << cout.goodbit << endl;
    cout << cout.good() << endl;
    cout << cout.eofbit << endl;
    cout << cout.eof() << endl;
    cout << cout.badbit << endl;
    cout << cout.bad() << endl;
    cout << cout.failbit << endl;
    cout << cout.fail() << endl;
    cout << cout.rdstate() << endl;
}

