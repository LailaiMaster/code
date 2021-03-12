#include <io_utils.h>
#include <string.h>
#include <stdlib.h>

//=============== 字符串的拆分：strtok ===============
int main() {
    char string[] = "C, 1972; C++, 1983; Java, 1995; Rust, 2010; Kotlin, 2011";

    typedef struct {
        char *name;
        int year;
    } Language;

    const char *language_break = ";";//用 ; 来拆分语言
    const char *field_break = ",";//用 , 来拆分年份

    int language_capacity = 3;//先假定有三个，后续可以动态分配
    int language_size = 0;

    Language *languages = malloc(sizeof(Language) * language_capacity);
    if (!languages) {
        abort();
    }

    char *next = strtok(string, field_break);//第一次拆分，next 指向 string 的首地址，然后将 string 中遇到的 field_break 替换为 NULL，此时 next = "C":

    while (next) {
        Language language;
        language.name = next;

        next = strtok(NULL, language_break);//strtok 是有状态函数，会记住之前拆分到的字符，如果第一个参数传 NULL，会接着之前的字符串继续拆分。【非线程安全】
        if (next) {
            language.year = atoi(next);

            if (language_size + 1 >= language_capacity) {
                language_capacity *= 2;//扩容
                languages = realloc(languages, sizeof(Language) * language_capacity);
                if (!languages) {
                    abort();
                }
            }

            languages[language_size++] = language;
            next = strtok(NULL, field_break);
        }
    }

    PRINTLNF("languages: %d", language_size);
    PRINTLNF("languages capacity: %d", language_capacity);

    for (int i = 0; i < language_size; ++i) {
        PRINTLNF("Language[name=%s, year=%d]", languages[i].name, languages[i].year);
    }

    free(languages);

    return 0;
}
