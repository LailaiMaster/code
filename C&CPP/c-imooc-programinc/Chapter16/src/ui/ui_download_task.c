#include "ui/ui_download_task.h"
#include <utils/time_utils.h>
#include "utils/unit_utils.h"
#include "utils/object_utils.h"

#define REFRESH_INTERVAL 1000

static char const *GetStatusText(DownloadTask *task) {
  char const *status_text;
#define CASE_STATUS(status) case status: status_text = status##_TEXT; break;

  switch (task->task_info.status) {
    CASE_STATUS(STATUS_READY)
    CASE_STATUS(STATUS_PAUSED)
    CASE_STATUS(STATUS_DOWNLOADING)
    CASE_STATUS(STATUS_COMPLETED)
    CASE_STATUS(STATUS_ERROR)
    default:status_text = "";
  }
  return status_text;
}

static float GetProgress(DownloadTask *task) {
  float progress;
  if (task->task_info.size > 0) {
    progress = task->task_info.progress * 100.0f / task->task_info.size;
  } else {
    progress = 0;
  }
  return progress;
}

DownloadTask *CreateDownloadTask(TaskListContext *task_list_context, TaskInfo *task_info) {
  CREATE_OBJECT_CLEANED(DownloadTask, download_task);
  download_task->task_list_context = task_list_context;
  memcpy(&download_task->task_info, task_info, sizeof(TaskInfo));
  download_task->request_handler = NULL;

  char filesize_display_buffer[12];
  FormatByte(filesize_display_buffer, task_info->size);

  GtkTreeIter iterator;
  gtk_list_store_append(task_list_context->task_store, &iterator);

  gtk_list_store_set(task_list_context->task_store, &iterator,
                     COLUMN_FILENAME,  task_info->filename,
                     COLUMN_SIZE, filesize_display_buffer,
                     COLUMN_PROGRESS, GetProgress(download_task),
                     COLUMN_STATUS, GetStatusText(download_task),
                     COLUMN_ESTIMATE_TIME, "",
                     COLUMN_TRANSFER_RATE, "",
                     COLUMN_CREATE_TIME, task_info->create_time,
                     COLUMN_ID, task_info->id,
                     -1);

  GtkTreePath *path = gtk_tree_model_get_path(GTK_TREE_MODEL(task_list_context->task_store), &iterator);
  download_task->row_reference = gtk_tree_row_reference_new(GTK_TREE_MODEL(task_list_context->task_store),path);
  gtk_tree_path_free(path);
  return download_task;
}

void DestroyDownloadTask(DownloadTask *download_task) {
  RemoveDownloadTaskFromList(download_task);
  DestroyTaskInfoContent(&download_task->task_info);
  free(download_task);
}

void RemoveDownloadTaskFromList(DownloadTask *download_task) {
  if(download_task->row_reference) {

    GtkTreePath *path = gtk_tree_row_reference_get_path(download_task->row_reference);
    GtkTreeIter iterator;

    gtk_tree_model_get_iter(GTK_TREE_MODEL(download_task->task_list_context->task_store), &iterator, path);
    gtk_list_store_remove(GTK_LIST_STORE(download_task->task_list_context->task_store), &iterator);

    gtk_tree_path_free(path);

    gtk_tree_row_reference_free(download_task->row_reference);
    download_task->row_reference = NULL;
  }
}

static int UpdateDownloadTaskProgress(DownloadTask *download_task) {
  if(download_task->row_reference != NULL) {
    char transfer_rate_text[12] = {0};
    char estimate_time_text[12] = {0};

    if(download_task->task_info.status == STATUS_DOWNLOADING) {
      uint64_t current_time = TimeInMillisecond();
      uint64_t update_interval = current_time - download_task->last_update_time;

      if(update_interval > REFRESH_INTERVAL) {
        uint64_t transfer_rate = (download_task->task_info.progress - download_task->last_progress) * 1000 / update_interval;
        uint64_t estimate_time = transfer_rate == 0 ? 0 : (download_task->task_info.size - download_task->task_info.progress) / transfer_rate;

        download_task->last_update_time = current_time;
        download_task->last_progress = download_task->task_info.progress;

        FormatByteRate(transfer_rate_text, transfer_rate);
        FormatTime(estimate_time_text, estimate_time);
      } else {
        return FALSE;
      }
    }

    GtkTreePath *path = (GtkTreePath *) gtk_tree_row_reference_get_path(download_task->row_reference);
    GtkTreeIter iterator;
    gtk_tree_model_get_iter(GTK_TREE_MODEL(download_task->task_list_context->task_store), &iterator, path);

    gtk_list_store_set(
        download_task->task_list_context->task_store, &iterator,
        COLUMN_PROGRESS, GetProgress(download_task),
        COLUMN_ESTIMATE_TIME, estimate_time_text,
        COLUMN_TRANSFER_RATE, transfer_rate_text,
        -1);

    gtk_tree_model_row_changed(GTK_TREE_MODEL(download_task->task_list_context->task_store), path, &iterator);

    gtk_tree_path_free(path);
  }
  return FALSE;
}

void UpdateDownloadTaskWithStatus(DownloadTask *download_task, int status) {
  if(download_task->task_info.status == STATUS_REMOVING) {
    if(download_task->task_info.id != INVALID_ID) {
      download_task->task_info.status = status;
      UpdateTaskInfo(&download_task->task_info);
    }

    download_task->task_list_context->downloading_task_size--;
    DestroyDownloadTask(download_task);
  } else if(download_task->task_info.status != status) {
    PRINTLNF("Download task(%lld), status: %d -> %d",
             download_task->task_info.id,
             download_task->task_info.status,
             status);

    if(download_task->task_info.status == STATUS_DOWNLOADING) {
      download_task->task_list_context->downloading_task_size--;
    }

    download_task->task_info.status = status;

    if(status == STATUS_DOWNLOADING) {
      download_task->last_progress = download_task->task_info.progress;
      download_task->last_update_time = TimeInMillisecond();
      download_task->task_list_context->downloading_task_size++;
    } else {
      UpdateDownloadTaskProgress(download_task);
    }

    GtkTreePath *path = (GtkTreePath *) gtk_tree_row_reference_get_path(download_task->row_reference);
    GtkTreeIter iterator;
    gtk_tree_model_get_iter(GTK_TREE_MODEL(download_task->task_list_context->task_store), &iterator, path);

    gtk_list_store_set(
        download_task->task_list_context->task_store, &iterator,
        COLUMN_STATUS, GetStatusText(download_task),-1);

    gtk_tree_model_row_changed(GTK_TREE_MODEL(download_task->task_list_context->task_store), path, &iterator);

    gtk_tree_path_free(path);

    UpdateTaskInfo(&download_task->task_info);

  } else {
    PRINTLNF("Download task(%lld), status not changed: %d", download_task->task_info.id, status);
  }
}

int OnDownloadCompleted(DownloadTask *task) {
  UpdateDownloadTaskWithStatus(task, STATUS_COMPLETED);
  return FALSE;
}

int OnDownloadCancelled(DownloadTask *task) {
  UpdateDownloadTaskWithStatus(task, STATUS_PAUSED);
  return FALSE;
}

int OnDownloadFailed(DownloadTask *task) {
  UpdateDownloadTaskWithStatus(task, STATUS_ERROR);
  return FALSE;
}

void OnProgressUpdated(DownloadTask *download_task, uint64_t current_bytes, uint64_t total_bytes) {
  download_task->task_info.progress = current_bytes + download_task->task_info.size - total_bytes;
  gdk_threads_add_idle(G_SOURCE_FUNC(UpdateDownloadTaskProgress), download_task);
}