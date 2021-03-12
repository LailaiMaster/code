#include <io_utils.h>
#include <string.h>

//=============== 字符串的连接 ===============
int main() {
  char src[] = "HelloWorld";
  char dest[20] = "C said: ";
  //strcat(dest, src); //往 dest 后面追加内容，dest 要有足够的空间，要求 dest 是可修改的。
  strcpy(dest + strlen(dest), src);//将内容复制到指定位置。
  puts(dest);
  return 0;
}
