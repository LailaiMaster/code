/*
 ============================================================================

 Author      : Ztiany
 Description : 迭代器

 ============================================================================
 */


#include <cstdlib>
#include <string>
#include <iostream>
#include <vector>

using namespace std;

//1：使用迭代器
static void useIterator() {

    string s1("abcdefg hello!!");

    //1：第一个字符变大写
    if (s1.begin() != s1.end()) {
        auto it = s1.begin();
        *it = toupper(*it);
    }
    cout << "s1 = " << s1 << endl;

    //2：所有的字符变大写
    for (auto it = s1.begin(); it != s1.end(); it++) {
        if (!isspace(*it)) {
            *it = toupper(*it);
        }
    }
    cout << "s1 = " << s1 << endl;
}

//2：迭代器类型
static void iteratorType() {
    //常量迭代器
    const string string1 = "abc";
    string::const_iterator iterator1 = string1.begin();
    //普通迭代器
    string string2 = "abc";
    string::iterator iterator2 = string2.begin();
    //auto
    auto it3 = string1.begin();
}

//3：解引用操作，箭头操作符
static void dereference() {
    vector<string> str_vector;
    for (char i = 'a'; i < 'z'; ++i) {
        string str = to_string(i);
        str_vector.push_back(str);
    }
    //->操作符把解引用和成员操作符结合在一起
    auto it = str_vector.begin();
    for (; it != str_vector.end(); it++) {
        cout << *it << "-" << (it->size()) << endl;
    }

    //注意：但凡使用了迭代器循环体，就不要向迭代器所属容器添加元素了。
}

void constIterator() {
    vector<int> vector1(1, 10);
    const vector<int> vector2(1, 10);
    vector<int>::iterator begin1 = vector1.begin();
    vector<int>::iterator end1 = vector1.end();
    vector<int>::const_iterator cbegin1 = vector1.cbegin();
    vector<int>::const_iterator cend1 = vector1.cend();

    vector<int>::const_iterator begin2 = vector2.begin();
    vector<int>::const_iterator end2 = vector2.end();
}

void midIterator() {
    vector<int> ivec;
    for (int i = 0; i < 10; ++i) {
        ivec.push_back(i);
    }

    cout << "ivec.size " << ivec.size() << endl;

    auto mid = ivec.begin() + ivec.size() / 2;
    auto end = ivec.end();

    for (auto begin = ivec.begin(); begin != mid; ++begin) {
        cout << "element " << *begin << endl;
    }
}

int main() {
    //useIterator();
    //iteratorType();
    //dereference();
    //constIterator();
    midIterator();
    return EXIT_SUCCESS;
}