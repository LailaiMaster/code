#include <io_utils.h>
#include <tinycthread.h>
#include <time_utils.h>

tss_t count_key;

void MyFree(void *ptr) {
  PRINTLNF("Free %#x", ptr);
  free(ptr);
}

int Counter(int *step) {
  int *count = malloc(sizeof(int));
  *count = 0;
  if (tss_set(count_key, count) == thrd_success) {
    for (int i = 0; i < 1000000; ++i) {
      *count += *step;
    }
  }
  PRINT_INT(*count);
  //PRINT_INT(*((int *)tss_get(count_key)));
  return 0;
}

int main(void) {
  if(tss_create(&count_key, MyFree) == thrd_success) {
    thrd_t t_1, t_2;
    int step_1 = 1, step_2 = 2;
    thrd_create(&t_1, Counter, &step_1);
    thrd_create(&t_2, Counter, &step_2);

    tss_delete(count_key);

    thrd_join(t_1, NULL);
    thrd_join(t_2, NULL);

    puts("t_1/t_2 ended.");

    PRINTLNF("count_key deleted.");
  }
}