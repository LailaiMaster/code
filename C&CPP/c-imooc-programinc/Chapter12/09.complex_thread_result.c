#include <io_utils.h>
#include <tinycthread.h>
#include <stdlib.h>

typedef struct {
  int a;
  char *b;
} ComplexResult;

typedef struct {
  int arg;
  ComplexResult result;
} ComplexArg;


int ComplexReturnValue(ComplexArg *arg) {
  ComplexResult *complex_result = &arg->result;
  complex_result->a = 2;
  complex_result->b = "HelloWorld";
  return 0;
}

int main(void) {
  ComplexArg complex_arg = {.arg = 1};
  thrd_t t;
  thrd_create(&t, ComplexReturnValue, &complex_arg);

  int result;
  thrd_join(t, &result);
  ComplexResult *complex_result = &complex_arg.result;
  PRINT_INT(complex_result->a);
  puts(complex_result->b);
}