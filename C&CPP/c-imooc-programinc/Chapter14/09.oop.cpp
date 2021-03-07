#include <iostream>

struct Creature {
  char const *name;
  int age;
};

struct Human: Creature {
  char const *address;
  int gender;
};

struct Student: Human {
  char const *student_number;
  char const *school;
};

using namespace std;

int main() {
  cout << sizeof(Creature) << endl;
  cout << sizeof(Human) << endl;
  cout << sizeof(Student) << endl;

  Human human;
  human.name = "bennyhuo";
  human.age = 30;
  human.address = "beijing";
  human.gender = 1;
  return 0;
}
