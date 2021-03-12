#include <stdio.h>
#include <io_utils.h>
#include <time_utils.h>
#include <locale.h>

#define COPY_SUCCESS 0
#define COPY_ILLEGAL_ARGUMENTS -1
#define COPY_SRC_OPEN_ERROR -2
#define COPY_SRC_READ_ERROR -3
#define COPY_DEST_OPEN_ERROR -4
#define COPY_DEST_WRITE_ERROR -5
#define COPY_UNKNOWN_ERROR -100

int CopyFile(char const *src, char const *dest) {
    if (!src || !dest) {
        return COPY_ILLEGAL_ARGUMENTS;
    }

    FILE *src_file = fopen(src, "r");
    if (!src_file) {
        return COPY_SRC_OPEN_ERROR;
    }

    FILE *dest_file = fopen(dest, "w");
    if (!dest_file) {
        fclose(src_file);
        return COPY_DEST_OPEN_ERROR;
    }

    int result;

    while (1) {
        int next = fgetc(src_file);
        if (next == EOF) {
            if (ferror(src_file)) {
                result = COPY_SRC_READ_ERROR;
            } else if (feof(src_file)) {
                result = COPY_SUCCESS;
            } else {
                result = COPY_UNKNOWN_ERROR;
            }
            break;
        }

        if (fputc(next, dest_file) == EOF) {
            result = COPY_DEST_WRITE_ERROR;
            break;
        }
    }

    fclose(src_file);
    fclose(dest_file);

    return result;
}

#define BUFFER_SIZE 512

int CopyFile2(char const *src, char const *dest) {
    if (!src || !dest) {
        // 参数至少有一个为 NULL
        return COPY_ILLEGAL_ARGUMENTS;
    }

    FILE *src_file = fopen(src, "r");
    if (!src_file) {
        // src 打开失败
        return COPY_SRC_OPEN_ERROR;
    }

    FILE *dest_file = fopen(dest, "w");
    if (!dest_file) {
        // dest 打开失败，记得关闭 src
        fclose(src_file);
        return COPY_DEST_OPEN_ERROR;
    }

    int result = COPY_SUCCESS;
    char buffer[BUFFER_SIZE];
    char *next;

    while (1) {
        next = fgets(buffer, BUFFER_SIZE, src_file);
        if (!next) {
            if (ferror(src_file)) {
                result = COPY_SRC_READ_ERROR;
            } else if (feof(src_file)) {
                result = COPY_SUCCESS;
            } else {
                result = COPY_UNKNOWN_ERROR;
            }
            break;
        }

        if (fputs(next, dest_file) == EOF) {
            result = COPY_DEST_WRITE_ERROR;
            break;
        }
    }

    fclose(src_file);
    fclose(dest_file);
    return result;
}

//=============== 文件复制：版本2——按行读取 ===============
int main() {
    /*
     * 1. 中文路径可以用 wfopen 函数，fopen只支持ASCII文件名。或者可以参考 https://www.cnblogs.com/zhugaopeng/p/9090980.html。
     * 2. msvc 下，fgets/fgetc 都会加锁，fgets 读一行加一次锁，fgetc 没读一次字符加一次锁。
     * 3. linux 下，fgetc 不一定会加锁，取决于文件本身的属性，且默认不加锁。
     * 4. copy file 2 使用的是 fgets/fgetc/fputs/fputc 类函数，只能处理文本文件，因为它们会对 \0\n 等数据进行特殊处理，而在二进制文件中，任何数据都不需要进行特殊处理。
     */
    setlocale(LC_ALL, "zh_CN.utf-8");
    const int size = 2;
//    char *srcs[] = {"data/io_utils.h", "data/三国演义.txt"};
//    char *dsts[] = {"data_copy/io_utils.h", "data_copy/三国演义.txt"};
    char *srcs[] = {"data/io_utils.h", "data/sanguo_utf8.txt"};
    char *dsts[] = {"data_copy/io_utils.h", "data_copy/sanguo_utf8.txt"};

    //按字符读取
    for (int i = 0; i < size; ++i) {
        TimeCost(NULL);
        CopyFile(srcs[i], dsts[i]);
        TimeCost(srcs[i]);
        PRINT_IF_ERROR("CopyFile: %s", srcs[i]);
    }

    //按行读取
    for (int i = 0; i < size; ++i) {
        TimeCost(NULL);
        CopyFile2(srcs[i], dsts[i]);
        TimeCost(srcs[i]);
        PRINT_IF_ERROR("CopyFile2: %s", srcs[i]);
    }
    return 0;
}
