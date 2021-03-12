#include <stdio.h>
#include <stdarg.h>

/**
 * Tower of Hanoi
 * @param n  The count of plates.
 * @param src The source of the plates to move from.
 * @param dest The destination of the plates to move to.
 * @param tmp The temporary place to use.
 */
void Move(int n, char src, char dest, char tmp) {
  if (n == 0) return;
  else if (n == 1) printf("%c --> %c\n", src, dest);
  else {
    Move(n - 1, src, tmp, dest);
    Move(1, src, dest, tmp);
    Move(n - 1, tmp, dest, src);
  }
}

/**案例：汉诺塔*/
int main(void) {
  Move(3, 'A', 'C', 'B');
  return 0;
}