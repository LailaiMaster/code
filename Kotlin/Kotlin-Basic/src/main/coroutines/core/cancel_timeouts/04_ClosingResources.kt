package core.cancel_timeouts

import kotlinx.coroutines.*

fun main() = runBlocking {
    /*
    不使用 try catch 也不会抛出异常导致程序崩溃
    val job = launch {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
    }
    */
    //sampleStart
    val job = launch {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } catch (e: CancellationException) {
            e.printStackTrace()
        } finally {
            //如果暂停状态被取消，将会抛出异常，所以需要在finally中释放资源
            println("I'm running finally")
        }
    }
    delay(1300L) // delay a bit
    println("me.ztiany.tools.main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("me.ztiany.tools.main: Now I can quit.")
//sampleEnd
}