/*
 ============================================================================
 
 Author      : Ztiany
 Description : 可变参数

 ============================================================================
 */

#include <string>
#include <initializer_list>
#include <iostream>

using namespace std;

//可变参数类型
//initializer_list是标准中的类型，与vector类似， 它是一个模板，initializer_list中的对象永远是常量
//initializer_list可用于传递类型相同数量不定的参数
static void initializerSample() {
    initializer_list<int> lst1;
    initializer_list<int> lst2{1, 2, 3, 4, 5, 6};
    initializer_list<int> lst3(lst2);//拷贝一个initializer_list，不会拷贝列表中的元素，拷贝后，原始列表和副本共享元素
    initializer_list<int> lst4 = lst2;//赋值一个initializer_list，不会拷贝列表中的元素，拷贝后，原始列表和副本共享元素
}

static void initializerParameter(initializer_list<string> il) {
    for (auto begin = il.begin(); begin != il.end(); begin++) {
        cout << *begin << " ";
    }
    cout << endl;
}

static void invokeInitializer() {
    initializerParameter({"CBA", "CBA", "CBA"});
    initializerParameter({"NBA", "NBA"});
}

static void sample(...){

}

int main() {
    initializerSample();
    initializerSample();
}