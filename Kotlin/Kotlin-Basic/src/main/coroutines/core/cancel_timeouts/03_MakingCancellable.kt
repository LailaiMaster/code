package core.cancel_timeouts


/*
有两种方法可以让计算代码变得可以被取消：

1. 定期调用挂起函数来检查取消，yield 是一个不错的选择。（yield 函数的作用主要是检查所在协程的状态，如果已经取消，则抛出取消异常予以响应。此外，它还会尝试出让线程的执行权，给其他协程提供执行机会。）
2. 在协程内明确地检查自己的取消状态。

下面是方式2的示例。
 */
import kotlinx.coroutines.*

fun main() = runBlocking {
    //sampleStart
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // cancellable computation loop
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("me.ztiany.tools.main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("me.ztiany.tools.main: Now I can quit.")
    //sampleEnd
}