#include <stdio.h>
#include <io_utils.h>
#include <locale.h>
#include <sys/stat.h>

/*MSVC 的 FILE 结构，直接 copy 过来的*/
typedef struct __crt_stdio_stream_data {
    union {
        FILE _public_file;
        char *_ptr;
    };

    char *_base;
    int _cnt;
    long _flags;
    long _file;
    int _charbuf;
    int _bufsiz;
    char *_tmpfname;

} WinFile;

long GetFileSize(char const *filename) {
    //4GB 以上的文件，需要使用 struct _stat_64 版本
    struct stat st;
    stat(filename, &st);
    return st.st_size;
}

int IsDirectory(char const *filename) {
    struct stat st;
    stat(filename, &st);
    return st.st_mode & S_IFDIR;
}

//=============== 其他文件操作 ===============
void remove_file() {
    int result = remove("data_copy/io_utils.h");
    PRINT_INT(result);
    result = remove("data_copy/io_utils2.h");
    PRINT_INT(result);
}

void rename_file() {
    setlocale(LC_ALL, "zh_CN.utf-8");
    rename("data_copy/三国演义.txt", "data_copy/sanguo.txt");
}

void get_wsvc_temp_file_path() {
    FILE *file = tmpfile();
    WinFile *win_file = (WinFile *) file;
    puts(win_file->_tmpfname);
    fclose(file);
}

/*
 * 1. remove 删除文件
 * 2. rename 文件重命名/移动
 * 3. tmpfile 创建临时文件
 * 4. 获取文件大小
 * 5. 判断文件是否为目录
 */
int main() {
    //remove_file();
    //rename_file();
    //get_wsvc_temp_file_path();

    PRINT_LONG(GetFileSize("data/sanguo_utf8.txt"));
    PRINT_INT(IsDirectory("data/sanguo_utf8.txt"));
    PRINT_INT(IsDirectory("data"));
    return 0;
}
