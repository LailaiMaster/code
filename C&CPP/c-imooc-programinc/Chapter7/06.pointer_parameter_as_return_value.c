#include <stdio.h>
#include <io_utils.h>

int SumIntArray(int array[], int length) {
  int sum = 0;
  for (int i = 0; i < length; ++i) {
    sum += array[i];
  }
  return sum;
}

void SumIntArray2(int array[], int length, int *sum) {
  *sum = 0;
  for (int i = 0; i < length; ++i) {
    *sum += array[i];
  }
}

__int128 TestBigValue() {
  return 0;
}

typedef struct {
  char *name;
  int gender;
  int age;
  char *school_name;
} Student;

Student TestStruct() {
  Student student = {"bennyhuo", 1, 30, "bupt"};
  return student;
}

int main() {
  int array[] = {0, 1, 2, 3, 4};
  int sum = SumIntArray(array, 5);
  int sum2;
  SumIntArray2(array, 5, &sum2);

  PRINT_INT(sum);
  PRINT_INT(sum2);

  __int128 big_int = TestBigValue();

  Student student = TestStruct();
  return 0;
}
