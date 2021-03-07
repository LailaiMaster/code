#include "common/sqlite_manager.h"
#include "utils/io_utils.h"
#include <stdio.h>
#include <sqlite3.h>
#include <stdlib.h>

static sqlite3 *db;

static int Sqlite3PrepareWithParameters(sqlite3_stmt **p_stmt, char const *sql_format, va_list args) {
  char *sql = strdup(sql_format);
  int sql_length = strlen(sql);
  char parameter_types[20];
  int parameter_type_count = 0;

  for (int i = 0; i < sql_length; ++i) {
    if (sql[i] == '$') {
      if(sql[i + 1] != '$') {
        parameter_types[parameter_type_count++] = sql[i + 1];
        sql[i] = '?';
      }
      sql[i + 1] = ' ';
    }
  }

  PRINTLNF("DB Execute: %s", sql);
  int err = sqlite3_prepare_v2(db, sql, -1, p_stmt, NULL);

  free(sql);

  if (err != SQLITE_OK) {
    PRINTLNF("prepare failed: %s", sqlite3_errmsg(db));
    return RESULT_FAILURE;
  }

  for (int i = 0; i < parameter_type_count; ++i) {
    switch (parameter_types[i]) {
      case 'c': {
        char arg = va_arg(args, int);
        char parameter_holder[2] = {arg};
        sqlite3_bind_text(*p_stmt, i + 1, parameter_holder, -1, NULL);
        break;
      }
      case 's': {
        sqlite3_bind_text(*p_stmt, i + 1, va_arg(args, char *), -1, NULL);
        break;
      }
      case 'd': {
        int arg = va_arg(args, int);
        sqlite3_bind_int64(*p_stmt, i + 1, arg);
        break;
      }
      case 'f': {
        sqlite3_bind_double(*p_stmt, i + 1, va_arg(args, double));
        break;
      }
      default: {
        PRINTLNF("Unsupported parameter type: %c.", parameter_types[i]);
        exit(-1);
      }
    }
  }

  return RESULT_OK;
}

int OpenDataBase(char *path) {
  if (sqlite3_open(path, &db) != SQLITE_OK) {
    PRINTLNF("Can't open database: %s", sqlite3_errmsg(db));
    CloseDataBase();
    return RESULT_FAILURE;
  }
  return RESULT_OK;
}

int ExecuteSql(char *sql_format, ...) {
  if (db) {
    int result;
    sqlite3_stmt *stmt;

    va_list args;
    va_start(args, sql_format);
    if (Sqlite3PrepareWithParameters(&stmt, sql_format, args) == RESULT_OK) {
      if (sqlite3_step(stmt) != SQLITE_DONE) {
        PRINTLNF("execution failed: %s", sqlite3_errmsg(db));
        result = RESULT_FAILURE;
      } else {
        result = RESULT_OK;
      }
      sqlite3_finalize(stmt);
    } else {
      result = RESULT_FAILURE;
    }
    va_end(args);

    return result;
  } else {
    return RESULT_FAILURE;
  }
}

int QuerySingle(QuerySingleResult *query_single_result, char *sql_format, ...) {
  if (db) {
    int result;
    sqlite3_stmt *stmt;

    va_list args;
    va_start(args, sql_format);
    if (Sqlite3PrepareWithParameters(&stmt, sql_format, args) == RESULT_OK) {
      if (sqlite3_step(stmt) == SQLITE_ROW) {
        query_single_result->data = malloc(query_single_result->element_size);
        query_single_result->data_retriever(query_single_result->data, stmt);
        result = RESULT_OK;
      } else {
        PRINTLNF("execution failed: %s", sqlite3_errmsg(db));
        result = RESULT_FAILURE;
      }
      sqlite3_finalize(stmt);
    } else {
      result = RESULT_FAILURE;
    }
    va_end(args);

    return result;
  } else {
    return RESULT_FAILURE;
  }
}

int QueryMany(QueryManyResult *query_result, char *sql_format, ...) {
  if (db) {
    int result;
    sqlite3_stmt *stmt;
    va_list args;
    va_start(args, sql_format);
    if (Sqlite3PrepareWithParameters(&stmt, sql_format, args) == RESULT_OK) {
      if (!query_result->array) {
        query_result->array = g_ptr_array_new_with_free_func(free);
      }

      while (1) {
        int sql_result = sqlite3_step(stmt);
        if (sql_result == SQLITE_ROW) {
          void *data = malloc(query_result->element_size);
          g_ptr_array_add(query_result->array, data);
          query_result->data_retriever(data, stmt);
        } else if(sql_result == SQLITE_ERROR){
          PRINTLNF("execution failed: %s", sqlite3_errmsg(db));
          result = RESULT_FAILURE;
          break;
        } else {
          result = RESULT_OK;
          break;
        }
      }
      sqlite3_finalize(stmt);
    } else {
      result  = RESULT_FAILURE;
    }
    va_end(args);
    return result;
  }
  return RESULT_FAILURE;
}

sqlite3_int64 GetLastInsertRowId() {
  return sqlite3_last_insert_rowid(db);
}

void CloseDataBase() {
  if (db) {
    sqlite3_close(db);
    db = NULL;
  }
}


