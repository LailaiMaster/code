/*
 ============================================================================
 
 Author      : Ztiany
 Description : run-time type identification, 运行时类型识别

 ============================================================================
 */
#include <string>
#include <iostream>

#ifndef CPLUSPLUS_PROGRAM_RTTI_H
#define CPLUSPLUS_PROGRAM_RTTI_H

class BaseWithVirtual {

private:
    std::string name;
    int age;
public:

    BaseWithVirtual(std::string &_name, int _age) : name(_name), age(_age) {

    }

    virtual void printInfo() {
        std::cout << "name = " << name << ", age = " << age << std::endl;
    }

    std::string getName() const {
        return name;
    }

    int getAge() const {
        return age;
    }

};

class DeriveWithVirtual : public BaseWithVirtual {
private:
    std::string address;
public:
    DeriveWithVirtual(std::string &_name, int _age, std::string &_address)
            : BaseWithVirtual(_name, _age), address(_address) {

    }

    void printInfo() override {
        std::cout << "name = " << getName() << ", age = " << getAge() << ", address = " << address << std::endl;
    }
};


class BaseWithoutVirtual {

private:
    std::string name;
    int age;
public:

    BaseWithoutVirtual(std::string &_name, int _age) : name(_name), age(_age) {

    }

    void printInfo() {
        std::cout << "name = " << name << ", age = " << age << std::endl;
    }

    std::string getName() const {
        return name;
    }

    int getAge() const {
        return age;
    }

};

class DeriveWithoutVirtual : public BaseWithoutVirtual {
private:
    std::string address;
public:
    DeriveWithoutVirtual(std::string &_name, int _age, std::string &_address)
            : BaseWithoutVirtual(_name, _age), address(_address) {

    }

    void printInfo() {
        std::cout << "name = " << getName() << ", age = " << getAge() << ", address = " << address << std::endl;
    }
};


#endif //CPLUSPLUS_PROGRAM_RTTI_H
