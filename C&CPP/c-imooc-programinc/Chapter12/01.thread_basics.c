#include <io_utils.h>
#include <tinycthread.h>

//=============== 线程基础 ===============
/*
 * C 标准中，直到 C11 才提供了对线程的支持，而在此之前线程就已经存在了，因此各个平台都有自己的实现方案。
 * 而目前 Windows/Linux 等平台都没有实现 C11 标准，因此这里会使用开源库 tinycthread  来体验 C11 线程功能【tinycthread 使用个平台的线程支持实现了 C11 规范】。
 *
 * 使用线程，可以充分利用多核 cpu 计算资源。
 *
 * Tinycthread 的策略
 *  1. msvc/mingw 使用 windows 线程 api。【mingw 也定义了 _WIN32 宏】
 *  2. 其他平台使用 posix 标准。
 *
 */
int main(void) {

    return 0;
}