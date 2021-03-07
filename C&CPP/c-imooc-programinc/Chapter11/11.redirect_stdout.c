#include <stdio.h>
#include <io_utils.h>
#include <time_utils.h>
#include <locale.h>

#if defined(__APPLE__) || defined(__linux__)
#  include <unistd.h>
#elif defined(_WIN32)
#  include <io.h>
#endif

void RedirectStdout(char const *filename) {
  static int saved_stdout_no = -1;

  if (filename) {
    if(saved_stdout_no == -1) {
      saved_stdout_no = dup(fileno(stdout));
    }

    fflush(stdout);
    freopen(filename, "a", stdout);
  } else {
    if (saved_stdout_no != -1) {
      fflush(stdout);
      dup2(saved_stdout_no, fileno(stdout));
      close(saved_stdout_no);
      saved_stdout_no = -1;
    }
  }
}

int main() {
//  freopen("output.log", "a", stdout);
//  puts("This will be written to file 'output.log'");
//  fclose(stdout);

  puts("1");
  RedirectStdout("output.log");
  puts("2");
  RedirectStdout(NULL);
  puts("3");
  RedirectStdout("output.log");
  puts("4");
  RedirectStdout(NULL);
  puts("end");

  // python x.py > output.log 2>&1
  return 0;
}
