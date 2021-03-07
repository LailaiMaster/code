#ifndef BASICC_INCLUDE_UI_UI_TASK_LIST_H_
#define BASICC_INCLUDE_UI_UI_TASK_LIST_H_

#include "model/task_info.h"
#include <gtk/gtk.h>
#include <stdint.h>

typedef struct TaskListContext {
  GtkWindow *window;
  GtkTreeSelection *task_selection;
  GtkListStore *task_store;
  GSList *download_task_list;
  int downloading_task_size;
} TaskListContext;

void InitTaskList(GtkBuilder *builder);

void DestroyTaskList();

void AddTaskToList(TaskInfo *task_info);

void RemoveSelectedTask();

void ResumeSelectedTask();

void PauseSelectedTask();
#endif //BASICC_INCLUDE_UI_UI_TASK_LIST_H_
