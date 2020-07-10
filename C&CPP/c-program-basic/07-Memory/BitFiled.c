/*
 ============================================================================
 
 Author      : Ztiany
 Description : 位字段

 ============================================================================
 */
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

int main() {

    struct box_props {
        bool opaque                   : 1;
        unsigned int fill_color       : 3;
        unsigned int                  : 4;
        bool show_border              : 1;
        unsigned int border_color     : 3;
        unsigned int border_style     : 2;
        unsigned int                  : 2;
    };

    struct book {
        int a;//4
        float b;//4
        double c;//8
    };

    printf("sizeof(box_props) = %zd\n", sizeof(struct box_props));
    printf("sizeof(book) = %zd\n", sizeof(struct book));

    struct box_props box1;
    box1.fill_color = 3;

    return EXIT_SUCCESS;
}

