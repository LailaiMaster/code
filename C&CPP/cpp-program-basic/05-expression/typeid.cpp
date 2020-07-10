/*
 ============================================================================
 
 Author      : Ztiany
 Description : typeid

 ============================================================================
 */
#include <cstdlib>
#include <iostream>

using namespace std;

int main() {

    int i = 1;
    long l = 1;

    cout << "typeid(i) = " << typeid(i).name() << endl;
    cout << "typeid(l) = " << typeid(l).name() << endl;

    if (typeid(i) == typeid(int)) {
        cout << "type of i is int" << endl;
    }

    return EXIT_SUCCESS;
}
