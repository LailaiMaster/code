#include <stdio.h>
#include <io_utils.h>

int main() {
  /*
   * struct <结构体名> {
   *  <成员类型> <成员名>;
   *  ...
   * } <结构体变量>;
   */

  typedef struct Company {
    char *name;
    char *id;
    char *location;
  } Company;

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

  struct Company company = {.name="imooc", .id="1212121"};
  struct Person person = {.name="bennyhuo", .id="12113322222", .company=&company,
      .company2={.name="imooc", .id="32323232"}
  };

  PRINT_INT(person.age);
  person.age = 30;

  //person.company->name
  //person.extra_value.extra

  PRINT_HEX(&person);

  Person *person_ptr = &person;

  puts(person_ptr->name);

  PRINT_INT(sizeof(Person));
  PRINT_INT(sizeof(person));

  struct {
    char *name;
    int age;
    char *id;
  } anonymous_person;

  Person person1 = {.name = "andy", .age = 20};
  PRINT_INT(person1.age);

  return 0;
}
