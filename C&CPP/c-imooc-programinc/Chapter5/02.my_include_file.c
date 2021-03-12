#include <stdio.h>
//使用双引号引入头文件，会先去本地搜索。
//#include "factorial.h"
#include <factorial.h>

//=============== 头文件的定义、实现与导入 ===============
int main() {
  printf("3! = %d\n", Factorial(3));
  return 0;
}
