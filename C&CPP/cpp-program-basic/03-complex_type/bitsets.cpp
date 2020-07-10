/*
 ============================================================================
 
 Author      : Ztiany
 Description : bitset（位集）

 ============================================================================
 */

#include <cstdlib>
#include <iostream>
#include <bitset>

int main() {

    std::bitset<32> flags("010101");

    std::cout << flags.test(0) << std::endl;
    std::cout << "sizeof(flags) " << sizeof(flags) << std::endl;

    return EXIT_SUCCESS;
}