#include "model/database_common.h"
#include "common/sqlite_manager.h"
#include "utils/io_utils.h"

#define TABLE_COUNT 1
static char *tables[TABLE_COUNT] = {
    "create table if not exists task_info"
    "("
    "   id integer primary key autoincrement,"
    "   url varchar(1024),"
    "   filename varchar(1024),"
    "   directory varchar(1024),"
    "   size integer,"
    "   progress real default 0,"
    "   status integer default 0,"
    "   resume_support integer default 0,"
    "   create_time timestamp default current_timestamp"
    ");",
};

int InitTables() {
  for (int i = 0; i < TABLE_COUNT; ++i) {
    char *create_table_sql = tables[i];
    if (ExecuteSql(create_table_sql)) {
      PRINTLNF("Failed to create table with: %s\n", create_table_sql);
      return RESULT_FAILURE;
    }
  }
  return RESULT_OK;
}