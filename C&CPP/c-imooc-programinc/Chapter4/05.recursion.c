#include <stdio.h>

unsigned int Factorial(unsigned int n) {
    if (n == 0) {
        return 1; // f(0) = 1
    } else {
        return n * Factorial(n - 1); // f(n) = nf(n - 1)
    }
}

unsigned int FactorialByIteration(unsigned int n) {
    unsigned int result = 1;
    for (unsigned int i = n; i > 0; --i) {
        result *= i;
    }
    return result;
}

unsigned int Fibonacci(unsigned int n) {
    if (n == 1 || n == 0) {
        return n; // f(0) = 0, f(1) = 1
    } else {
        return Fibonacci(n - 1) + Fibonacci(n - 2); // f(n) = f(n - 1) + f(n - 2)
    }
}

unsigned int FibonacciByIteration(unsigned int n) {
    if (n == 1 || n == 0) {
        return n; // f(0) = 0, f(1) = 1
    }

    unsigned int last = 0;
    unsigned int current = 1;

    for (int i = 0; i <= n - 2; ++i) {
        unsigned int temp = current;
        current += last;
        last = temp;
    }
    return current;
}


//=============== 函数的递归 ===============
int main(void) {
    //递归要有结束条件，不如递归太深，会对栈空间造成很大压力。
    printf("3! : %d\n", Factorial(3));
    printf("5! : %d\n", Factorial(5));
    printf("10! : %d\n", FactorialByIteration(10));

    printf("Fibonacci(3): %d\n", Fibonacci(3));
    printf("Fibonacci(5): %d\n", Fibonacci(5));
    printf("Fibonacci(10) : %d\n", Fibonacci(10));
    printf("Fibonacci(10) : %d\n", FibonacciByIteration(10));
    return 0;
}