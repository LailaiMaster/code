#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

#define ERROR 0
#define OK 1

typedef struct {
    int visibility;
    int allow_notification;
    int refresh_rate;
    int region;
    int font_size;
} Settings;

void PrintSettings(Settings *settings) {
    PRINT_INT(settings->visibility);
    PRINT_INT(settings->allow_notification);
    PRINT_INT(settings->refresh_rate);
    PRINT_INT(settings->region);
    PRINT_INT(settings->font_size);
}

int SaveSettings(Settings *settings, char *settings_file) {
    FILE *file = fopen(settings_file, "wb");
    if (file) {
        fwrite(&settings->visibility, sizeof(settings->visibility), 1, file);
        fwrite(&settings->allow_notification, sizeof(settings->allow_notification), 1, file);
        fwrite(&settings->refresh_rate, sizeof(settings->refresh_rate), 1, file);
        fwrite(&settings->region, sizeof(settings->region), 1, file);
        fwrite(&settings->font_size, sizeof(settings->font_size), 1, file);
        fclose(file);
        return OK;
    } else {
        perror("Failed to save settings.");
        return ERROR;
    }
}

void LoadingSettings(Settings *settings, char *settings_file) {
    FILE *file = fopen(settings_file, "rb");
    if (file) {
        fread(&settings->visibility, sizeof(settings->visibility), 1, file);
        fread(&settings->allow_notification, sizeof(settings->allow_notification), 1, file);
        fread(&settings->refresh_rate, sizeof(settings->refresh_rate), 1, file);
        fread(&settings->region, sizeof(settings->region), 1, file);
        fread(&settings->font_size, sizeof(settings->font_size), 1, file);
        fclose(file);
    } else {
        settings->visibility = 1;
        settings->allow_notification = 1;
        settings->refresh_rate = 30;
        settings->region = 86;
        settings->font_size = 18;
    }
}

int SaveSettings2(Settings *settings, char *settings_file) {
    FILE *file = fopen(settings_file, "wb");
    if (file) {
        fwrite(settings, sizeof(Settings), 1, file);
        fclose(file);
        return OK;
    } else {
        perror("Failed to save settings.");
        return ERROR;
    }
}

void LoadingSettings2(Settings *settings, char *settings_file) {
    FILE *file = fopen(settings_file, "rb");
    if (file) {
        fread(settings, sizeof(Settings), 1, file);
        fclose(file);
    } else {
        settings->visibility = 1;
        settings->allow_notification = 1;
        settings->refresh_rate = 30;
        settings->region = 86;
        settings->font_size = 18;
    }
}

//=============== 案例：字节读写-序列化和反序列化 ===============
/*
 * 1. 这个案例中 LoadingSettings2 和 SaveSettings 也能一起正常工作（不使用SaveSettings2），不过这纯属巧合，因为 Settings 中都是 int，没有发生字节对齐。
 * 2. 这个案例仅仅是实例，不可能用于生产中，因为毫无扩展性，后面 Setting 增加字段，肯定不行。
 * 3. 实际生成过程，一般使用 json/protobuf 之类的技术。
 *
 * 这个案例设计的目的是，让我们深刻理解 fwrite/fread 函数参数设计成 void* 的意图【高度的灵活性】。
 */
int main() {
    Settings settings;
    LoadingSettings2(&settings, "settings.bin");
    PrintSettings(&settings);
    settings.font_size = 40;
    settings.allow_notification = 2;
    SaveSettings2(&settings, "settings.bin");
    return 0;
}
