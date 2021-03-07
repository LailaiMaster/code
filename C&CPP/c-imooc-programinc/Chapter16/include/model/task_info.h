#ifndef BASICC_DOWNLOADER_TASK_INFO_H_
#define BASICC_DOWNLOADER_TASK_INFO_H_

#include <glib.h>
#include <sqlite3.h>

typedef struct sqlite3_stmt sqlite3_stmt;

#define STATUS_REMOVING -1
#define STATUS_ERROR 0
#define STATUS_READY  1
#define STATUS_PAUSED 2
#define STATUS_DOWNLOADING 3
#define STATUS_COMPLETED 4

#define STATUS_READY_TEXT "Ready" //0
#define STATUS_PAUSED_TEXT "Paused" //1
#define STATUS_DOWNLOADING_TEXT "Downloading" //2
#define STATUS_COMPLETED_TEXT "Completed" //3
#define STATUS_ERROR_TEXT "Error" //4

#define INVALID_ID -1

typedef struct {
  gint64 id;
  char *filename;
  char *directory;
  char *url;
  gint64 size;
  guint64 progress;
  guint status;
  char *create_time;
  gboolean resume_support;
} TaskInfo;

void TaskInfoDump(TaskInfo *task_info);

void DestroyTaskInfoContent(TaskInfo *task_info);

void DestroyTaskInfo(TaskInfo **task_info);

int InsertTaskInfo(TaskInfo *task_info);

void UpdateTaskInfo(TaskInfo *task_info);

void DeleteTaskInfo(TaskInfo *task_info);

TaskInfo *FindTaskInfoById(sqlite3_int64 id);

GPtrArray *ListTaskInfos();

#endif //BASICC_DOWNLOADER_TASK_INFO_H_
