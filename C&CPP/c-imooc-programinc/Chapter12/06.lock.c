#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

int count = 0;
mtx_t mutex;

int Counter(void *arg) {
  PRINTLNF("Counter.");
  for (int i = 0; i < 1000000; ++i) {
    mtx_lock(&mutex);
    count++;
    mtx_unlock(&mutex);

    // int temp = count, count = temp + 1, temp;
  }
  return 0;
}

int main(void) {
  mtx_init(&mutex, mtx_plain);
  TimeCost(NULL);

  thrd_t t_1, t_2;
  thrd_create(&t_1, Counter, NULL);
  thrd_create(&t_2, Counter, NULL);

  thrd_join(t_1, NULL);
  thrd_join(t_2, NULL);

  PRINT_INT(count);
  TimeCost("lock");
  mtx_destroy(&mutex);
}