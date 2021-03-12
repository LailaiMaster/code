#include <io_utils.h>
#include <tinycthread.h>

volatile int flag = 0;

volatile int a = 0;
int x = 0;

int T1(void *arg) {
  a = 2;
  flag = 1;
  a = a + 3;
  return 0;
}

int T2(void *arg) {
  while (!flag) {}
  x = a * a;
  return 0;
}

//=============== 并发工具：volatile ===============
/*
 * 1. volatile 从 C90 就开始有了，但是 C 标准并没有对并发程序进行支持，所以 volatile 也不是用于解决线程安全问题的【至少从 C 标准来看是这样的】。
 * 2. 不过 volatile 可以用于禁止重排序，即防止对被修饰的变量的读写进行优化，由此可以解决因为重排序而引发的线程安全问题。
 * 3. 但是 c 中的 volatile 不能解决可见性问题，不要和其他语言的 volatile 对比。
 * 4. 但是 MSVC 中赋予了 volatile 强制刷新缓存的予以，因此可以保证可见性。
 */
int main(void) {
  thrd_t t_1, t_2;
  thrd_create(&t_1, T1, NULL);
  thrd_create(&t_2, T2, NULL);

  thrd_join(t_1, NULL);
  thrd_join(t_2, NULL);

  PRINT_INT(x);
}