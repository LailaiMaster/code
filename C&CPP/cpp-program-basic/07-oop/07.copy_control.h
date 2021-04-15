/*
 ============================================================================
 
 Author      : Ztiany
 Description : 复制控制

 ============================================================================
 */
#include <iostream>
#include <string>

#ifndef CPLUSPLUS_PROGRAM_COPY_CONTROL_H
#define CPLUSPLUS_PROGRAM_COPY_CONTROL_H

class Book {
private:
    float price;
    std::string serialNo;

public:
    //定义构造函数
    Book(float _price, std::string &_serialNo) : price(_price), serialNo(_serialNo) {
        std::cout << "Book construct running" << std::endl;
    }

    //定义复制函构造数
    Book(const Book &book) : price(book.price), serialNo(book.serialNo) {
        std::cout << "Book copy construct running" << std::endl;
    }

    //定义拷贝操作符
    Book &operator=(const Book &book) {
        this->price = book.price;
        this->serialNo = book.serialNo;
        std::cout << "Book operator= running" << std::endl;
        return *this;
    }

};

#endif //CPLUSPLUS_PROGRAM_COPY_CONTROL_H
