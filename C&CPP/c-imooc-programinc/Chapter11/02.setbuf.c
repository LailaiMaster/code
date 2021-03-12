#include <stdio.h>
#include <io_utils.h>
#include <errno.h>
#include <string.h>

char std_buffer[BUFSIZ];

//=============== 文件流的缓存 ===============
int main() {
    //缓冲区的作用：优化 IO 速度，有了缓冲区 CPU 就一次性处理一个缓存区的数据，而不是每次 DMA 读多少，CPU 就处理多少。

    //=============== 设置文件流的缓存区：setbuf ===============
    //默认情况下，各个平台都有设置一定大小的文件缓存，我们也可以自己设置。缓存内存要跟文件流保持统一生命周期。
    //我们传入的 buffer 到底要多大呢？由于拿到一个指针后是无法从这个指针获取缓冲区的大小的，所以不管你传进去多大的缓存，该函数使用的大小都是一个默认值，定义在 BUFSIZ 中。
    //那就是说我们只能通过 setbuf 给文件流设置一个新的缓冲区，却无法改变其大小。所以 setbuf 仅仅是能让我们更灵活地控制文件缓冲区的开辟和释放。
    //而如果我们往 setbuf 传入 NULL 的话，就是关闭缓冲区的意思。
    setbuf(stdout, std_buffer);

    //=============== 设置文件流的缓存区：setvbuf 【推荐，可以设置大小】===============
    FILE *file = fopen("CMakeLists.txt", "r");
    char buf[8192];//缓冲区一定要比文件流存在得更久。
    //setbuf(file, NULL);

    if (file) {
        /*
         * mode 有三种值：
         *  IOFBF 换文件缓存，即全量缓存【二进制合适】
         *  IOLBF 按行缓存【文本时合适】
         *  IONFB 不缓存
         */
        setvbuf(file, buf, _IOLBF, 8192);
        puts("Open successfully.");
        int err = ferror(file);
        PRINT_INT(err);

        int eof = feof(file);
        PRINT_INT(eof);

        fflush(stdout);//主动清空缓冲区
        fclose(file);
    } else {
        PRINT_INT(errno);
        puts(strerror(errno));
        perror("fopen");
    }

    return 0;
}
