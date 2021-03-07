#include <stdio.h>
#include <io_utils.h>
#include <stddef.h>
//#pragma pack(2)
int main() {
  typedef struct Person {
    char *name;
    int age;
    char *id;
  } Person;

  struct Person person = {.name="bennyhuo", .id="12113322222"};

  PRINT_INT(person.age);
  person.age = 30;

  typedef struct {
    char a; // 1
    char b; // 1
//    __attribute((aligned(2))) int c; // 4 gcc only
//    _Alignas(8) int c; // 4 c11
    int c;
    short d; // 2
    double e; // 8
  } Align;

  typedef struct {
    char a; // 1
    char b; // 1
    short d; // 2
    int c; // 4
    double e; // 8
  } OptimizedAlign;


  Align align = {'a', 'b', 3, 4, 5.0};
  //PRINT_INT(_Alignof(align.c));
  PRINT_INT(offsetof(Align, e));
  return 0;
}
