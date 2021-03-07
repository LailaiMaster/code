#include <io_utils.h>
#include <tinycthread.h>
#include <stdatomic.h>

atomic_flag resume_flag = ATOMIC_FLAG_INIT;

int PrintNumbers(void *arg) {
  int current = 0;
  while(atomic_flag_test_and_set(&resume_flag)) {
    current++;
    PRINT_INT(current);
    thrd_sleep(&(struct timespec){.tv_sec=1}, NULL);
  }
  return current;
}

int main(void) {
  atomic_flag_test_and_set(&resume_flag);

  thrd_t t;
  thrd_create(&t, PrintNumbers, NULL);

  thrd_sleep(&(struct timespec){.tv_sec=5}, NULL);

  atomic_flag_clear(&resume_flag);
  int last_number;
  thrd_join(t, &last_number);
  PRINT_INT(last_number);
  return 0;
}