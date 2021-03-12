#include <stdio.h>

struct Creature {
    char const *name;
    int age;
};

struct Human {
    struct Creature base;
    char const *address;
    int gender;
};

struct Student {
    struct Human base;
    char const *student_number;
    char const *school;
};

//=============== 面向对象设计 ===============
/*
 * C 语言具有描述对象的能力，却没有描述对象直接继承关系的能力。不用可用给结构体的嵌套来模拟对象之间的关系，这也就是 GTK 的面向对象设计理念。
 */
int main(int argc, char *argv[]) {
    struct Human human = {
            .base = {
                    .name = "bennyhuo",
                    .age = 30
            },
            .gender = 1,
            .address = "beijing"
    };
    return 0;
}