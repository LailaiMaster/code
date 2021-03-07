#include <io_utils.h>
#include <tinycthread.h>

int SayHello(char *name) {
  PRINTLNF("Run in new thread[%#x]: Hello, %s", thrd_current(), name);
  return 1;
}

int main(void) {
  thrd_t new_thread;
  int result = thrd_create(&new_thread, SayHello, "C lang");
  if (result == thrd_success) {
    PRINTLNF("Run in Main thread[%#x], created new_thread[%#x]", thrd_current(), new_thread);
  } else {
    PRINTLNF("Run in Main Thread[%#x], failed to create new_thread", thrd_current());
  }
//  thrd_sleep(&(struct timespec) {.tv_sec = 0, .tv_nsec = 100000000}, NULL);

  int res;
  thrd_join(new_thread, &res);
  PRINTLNF("Run in Main Thread[%#x], result from thread[%#x]: %d", thrd_current(), new_thread, res);

//  thrd_detach(new_thread);
}