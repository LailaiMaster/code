#include "console_ui.h"
#include <stdlib.h>
#include <stdio.h>

UiContext *ui_context;

static void DisplayTextWithFile(char *text) {
  fputs(text, ui_context->text_displayer);
  fputc('\n', ui_context->text_displayer);

  fflush(ui_context->text_displayer);
}

static void ConsoleMain() {
  int next_input = getchar();

  while (next_input != EOF && next_input != 'q' && next_input != 'Q') {
    HandleInput(ui_context->context, next_input);
    next_input = getchar();
  }

  puts("Thank you");
}

static void InitConsole() {
  ui_context = malloc(sizeof(UiContext));
  ui_context->text_displayer = stdout;
  ui_context->context = CreateCalcContext();
  ui_context->context->display_text = DisplayTextWithFile;
}

static void DestroyConsole() {
  DestroyCalcContext(&ui_context->context);
  free(ui_context);
  ui_context = NULL;
}

int RunConsoleUi(int argc, char *argv[]) {
  InitConsole();
  ConsoleMain();
  DestroyConsole();
}