#include "model/task_info.h"
#include "common/sqlite_manager.h"
#include <sqlite3.h>
#include <stdlib.h>
#include <string.h>
#include "utils/io_utils.h"

static void TaskInfoRetriever(TaskInfo *task_info, sqlite3_stmt *result) {
  task_info->id = sqlite3_column_int64(result, 0);
  task_info->url = strdup((char *) sqlite3_column_text(result, 1));
  task_info->filename = strdup((char *) sqlite3_column_text(result, 2));
  task_info->directory = strdup((char *) sqlite3_column_text(result, 3));
  task_info->size = sqlite3_column_int64(result, 4);
  task_info->progress = sqlite3_column_int64(result, 5);
  task_info->status = sqlite3_column_int(result, 6);
  task_info->resume_support = sqlite3_column_int(result, 7);
  task_info->create_time = strdup((char *) sqlite3_column_text(result, 8));
}

int InsertTaskInfo(TaskInfo *task_info) {
  int result = ExecuteSql("insert into task_info(filename, directory, url, size, progress, status, resume_support) "
                          "values($s, $s, $s, $d, $d, $d, $d)",
                          task_info->filename,
                          task_info->directory,
                          task_info->url,
                          task_info->size,
                          task_info->progress,
                          task_info->status,
                          task_info->resume_support);

  if (result == RESULT_OK) {
    TaskInfo *task_info_from_db = FindTaskInfoById(GetLastInsertRowId());

    task_info->create_time = strdup(task_info_from_db->create_time);
    task_info->id = task_info_from_db->id;

    DestroyTaskInfo(&task_info_from_db);
  }

  return result;
}

void UpdateTaskInfo(TaskInfo *task_info) {
  ExecuteSql("update task_info set progress=$d, status=$d where id=$d",
             task_info->progress,
             task_info->status,
             task_info->id);
}

void DeleteTaskInfo(TaskInfo *task_info) {
  ExecuteSql("delete from task_info where id=$d", task_info->id);
  task_info->id = INVALID_ID;
}

TaskInfo *FindTaskInfoById(sqlite3_int64 id) {
  QuerySingleResult query_single_result = {
      .element_size = sizeof(TaskInfo),
      .data_retriever = (DataRetriever)TaskInfoRetriever
  };

  if (QuerySingle(&query_single_result, "select * from task_info where id=$d", id) == RESULT_OK) {
    return query_single_result.data;
  }
  return NULL;
}

GPtrArray *ListTaskInfos() {
  QueryManyResult query_many_result = {
      .element_size = sizeof(TaskInfo),
      .data_retriever = (DataRetriever)TaskInfoRetriever
  };
  QueryMany(&query_many_result, "select * from task_info");
  return query_many_result.array;
}

void DestroyTaskInfoContent(TaskInfo *task_info) {
  if (task_info) {
    free(task_info->filename);
    free(task_info->directory);
    free(task_info->url);
    free(task_info->create_time);
    memset(task_info, 0, sizeof(TaskInfo));
  }
}


void DestroyTaskInfo(TaskInfo **task_info) {
  if (task_info && *task_info) {
    free((*task_info)->filename);
    free((*task_info)->directory);
    free((*task_info)->url);
    free((*task_info)->create_time);

    free(*task_info);
    *task_info = NULL;
  }
}

void TaskInfoDump(TaskInfo *task_info) {
  PRINTLNF("TaskInfo(\n"
           "            id=%lld,\n"
           "            filename=%s,\n"
           "            directory=%s,\n"
           "            url=%s,\n"
           "            size=%lld,\n"
           "            progress=%lld,\n"
           "            status=%d,\n"
           "            resume_support=%d,\n"
           "            create_time=%s\n"
           "         )",
           task_info->id,
           task_info->filename,
           task_info->directory,
           task_info->url,
           task_info->size,
           task_info->progress,
           task_info->status,
           task_info->resume_support,
           task_info->create_time);
}
