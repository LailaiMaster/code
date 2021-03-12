#include "unit_test_ui.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

static UiContext *ui_context;

static void DisplayTextToBuffer(char *text) {
  sprintf(ui_context->text_displayer, text);
}

static void UnitTestMain() {
  FILE *case_file = fopen("cases.txt", "r");
  if (case_file) {

    char key;
    char output_result[MAX_CHAR + 1];

    while (fscanf(case_file, " %c,%s", &key, output_result) != EOF) {
      if (HandleInput(ui_context->context, key)) {

        if (strcmp(output_result, ui_context->text_displayer) == 0) {//输入输出校验
          printf("Input [%c]: Output: %s, OK\n", key, output_result);
        } else {
          printf("Input [%c]: Output: %s, Expect: %s, ERROR\n", key, ui_context->text_displayer, output_result);
          //exit(-1);
        }

      }
    }//loop end

    fclose(case_file);
  } else {
    perror("open case file");
  }
}

static void InitUnitTest() {
  char *const kOutputBuffer = malloc(MAX_CHAR + 1);

  ui_context = malloc(sizeof(UiContext));
  ui_context->text_displayer = kOutputBuffer;
  ui_context->context = CreateCalcContext();
  ui_context->context->display_text = DisplayTextToBuffer;
}

static void DestroyUnitTest() {
  DestroyCalcContext(&ui_context->context);
  free(ui_context->text_displayer);
  free(ui_context);
  ui_context = NULL;
}

int RunUnitTest(int argc, char *argv[]){
  InitUnitTest();
  UnitTestMain();
  DestroyUnitTest();
  return 0;
}