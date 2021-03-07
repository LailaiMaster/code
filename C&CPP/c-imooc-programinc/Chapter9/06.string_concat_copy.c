#include <io_utils.h>
#include <string.h>

int main() {
  char src[] = "HelloWorld";
  char dest[20] = "C said: ";
  //strcat(dest, src);
  strcpy(dest + strlen(dest), src);
  puts(dest);
  return 0;
}
