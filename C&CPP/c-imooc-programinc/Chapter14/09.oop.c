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