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

int main() {
  Settings settings;
  LoadingSettings2(&settings, "settings.bin");
  PrintSettings(&settings);
  settings.font_size = 40;
  settings.allow_notification = 2;
  SaveSettings2(&settings, "settings.bin");
  return 0;
}
