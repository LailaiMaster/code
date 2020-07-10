/*
 ============================================================================

 Author      : Ztiany
 Description : 文件流操作

 ============================================================================
 */
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <vector>

using namespace std;


void testFileIO();
void checkFileStatus1();
void checkFileStatus2();

int main() {
    //testFileIO();
    //checkFileStatus1();
    checkFileStatus2();
    return EXIT_SUCCESS;
}

void checkFileStatus2() {
    // for each file in the vector
    vector<string> files{"1.txt", "2.txt", "3.txt"};
    auto it = files.begin();
    string s;
    ifstream input;

    while (it != files.end()) {
        input.open(it->c_str());
        //是否正确地打开了文件
        if (!input) {
            cout << "bad file: " << it->c_str() << endl;
            ++it;
            input.close();
            input.clear();
            continue;
        }
        //开始处理
        while (input >> s) {
            cout << s << endl;
        }
        //除了下一个文件
        input.close();
        input.clear();
        ++it;
    }
}

void checkFileStatus1() {
    // for each file in the vector
    vector<string> files{"1.txt", "2.txt", "3.txt"};
    auto it = files.begin();
    string s;

    while (it != files.end()) {
        ifstream input(it->c_str());
        //是否正确地打开了文件
        if (!input) {
            cout << "bad file: " << it->c_str() << endl;
            ++it;
            continue;
        }
        //开始处理
        while (input >> s) {
            cout << s << endl;
        }
        //除了下一个文件
        ++it;
    }
}

//将数据从一文件复制到另一文件中
void copy(const string &file) {
    ifstream infile;
    ofstream outfile;
    infile.open(file);
    outfile.open("copy_" + file);

    //tips
    cout << "copy from " << file << " to copy_" << file << endl;

    //rdbuf() 可以实现一个流对象指向的内容用另一个流对象来输出
    outfile << infile.rdbuf();
    infile.close();
    outfile.close();
}

void writeFile(const string &file) {
    ofstream ofStream(file);
    if (!ofStream) {
        return;
    }
    ofStream << "Hello World" << endl;
    for (int i = 0; i < 300; ++i) {
        ofStream << i << endl;;
    }
    ofStream.close();
}

void readFile(const string &file) {
    ifstream ifStream(file);
    if (!ifStream) {
        return;
    }
    string content;
    //每次读取一个单词，以空格为标识，对于语句 if(ifStream)，如果文件流发生错误则返回 false。
    while (ifStream >> content) {
        cout << content << endl;
    }
    ifStream.close();
}

void testFileIO() {
    writeFile("test01.txt");
    readFile("test01.txt");
    copy("test01.txt");
}