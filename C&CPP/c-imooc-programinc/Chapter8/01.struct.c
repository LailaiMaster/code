#include <stdio.h>
#include <io_utils.h>

//=============== 结构体的定义 ===============
int main() {
    //=============== 语法 ===============
    /*
     * struct <结构体名> {
     *  <成员类型> <成员名>;
     *  ...
     * } <结构体变量>;
     */

    //=============== 结构体的定义【使用 typedef 定义结构体，后续就可以不用 struct 了】 ===============
    /*
     最简单的定义为【】：

            typedef struct {
                char *name;
                char *id;
                char *location;
            } Company;

    这里定义用了两个 Company，是因为下面用了struct Company company 这种方式来声明结构体。
     */
    typedef struct Company {
        char *name;
        char *id;
        char *location;
    } Company;

    //=============== 结构体的嵌套 ===============
    typedef struct Person {
        char *name;
        int age;
        char *id;
        Company *company;
        Company company2;
        struct {
            int extra;
            char *extra_str;
        } extra_value;
        struct Person *partner;
    } Person;

    //=============== 结构体的初始化 ===============
    struct Company company = {
            .name="imooc",
            .id="1212121"
    };
    struct Person person = {
            .name="bennyhuo",
            .id="12113322222", .
            company=&company,
            .company2={.name="imooc", .id="32323232"}
    };

    PRINT_INT(person.age);
    person.age = 30;
    person.company->name;
    person.extra_value.extra;
    PRINT_HEX(&person);

    Person *person_ptr = &person;
    puts(person_ptr->name);

    PRINT_INT(sizeof(Person));
    PRINT_INT(sizeof(person));

    //=============== 匿名结构体 ===============
    struct {
        char *name;
        int age;
        char *id;
    } anonymous_person;

    Person person1 = {.name = "andy", .age = 20};
    PRINT_INT(person1.age);
    return 0;
}
