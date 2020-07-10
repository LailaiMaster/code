/*
 ============================================================================
 
 Author      : Ztiany
 Description : 指针

 ============================================================================
 */

#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

int main(int argc, char *argv[]) {
    cout << "argc = " << argc << endl;

    int vi = 4;
    int *pv = &vi;

    string name = "Jordan";
    vector<int> vectorI{1, 2, 3, 4, 5};

    string *pName = &name;
    vector<int> *pVectorI = &vectorI;

    cout << "pName->size() = " << pName->size() << endl;

    return EXIT_SUCCESS;
}