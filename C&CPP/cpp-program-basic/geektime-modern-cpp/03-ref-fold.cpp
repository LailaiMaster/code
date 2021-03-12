/*
 ============================================================================
 
 Author      : Ztiany
 Description : 

 ============================================================================
 */
#include <cstdlib>
#include <cstdio>
#include <utility>

class shape {
public:
    virtual ~shape() {}
};

class circle : public shape {
public:
    circle() { puts("circle()"); }

    ~circle() { puts("~circle()"); }
};

class triangle : public shape {
public:
    triangle() { puts("triangle()"); }

    ~triangle() { puts("~triangle()"); }
};

void foo(const shape &) {
    puts("foo(const shape&)");
}

void foo(shape &&) {
    puts("foo(shape&&)");
}

void bar(const shape &s) {
    puts("bar(const shape&)");
    foo(s);
}

void bar(shape &&s) {
    puts("bar(shape&&)");
    foo(s);
}

template<typename T>
void bar(T &&s) {
    foo(std::forward<T>(s));
}

int main() {
    circle temp;
    bar(temp);
    bar(circle());
}
