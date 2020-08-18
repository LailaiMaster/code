package analyse

import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2019/8/25 11:28
 */
suspend fun main() {
    //sample1()
    sample2()
}

private suspend fun sample1() {
    log("1")
    val job = GlobalScope.launch(Dispatchers.Default + CoroutineName("你的名字")) {
        log("2 ${coroutineContext[CoroutineName]?.name}")
    }
    log("3")
    job.join()
}


private class DummyCoroutineContext : AbstractCoroutineContextElement(
        DummyCoroutineContext/*这里的DummyCoroutineContext引用的就是其伴生对象*/
) {

    companion object Key : CoroutineContext.Key<DummyCoroutineContext> {
        const val NAME = "DummyCoroutineContext"
    }

}

/*
 [me.ztiany.tools.main] <LogContinuation> Success(kotlin.Unit)
 [me.ztiany.tools.main] 1
 [me.ztiany.tools.main] <LogContinuation> Success(kotlin.Unit)
 [me.ztiany.tools.main] 2
 [me.ztiany.tools.main] 4
 [kotlinx.coroutines.DefaultExecutor] <LogContinuation> Success(kotlin.Unit)
 [kotlinx.coroutines.DefaultExecutor] 3
 [kotlinx.coroutines.DefaultExecutor] <LogContinuation> Success(Hello)
 [kotlinx.coroutines.DefaultExecutor] 5. Hello
 [kotlinx.coroutines.DefaultExecutor] 6

说明:

1. 首先，所有协程启动的时候，都会有一次 Continuation.resumeWith 的操作，这一次操作对于调度器来说就是一次调度的机会。（对应 launch 和 async）
2. delay 是挂起点，1000ms 之后需要继续调度执行该协程。（对应3）
3. await 也是挂起点（对应4）
 */
private suspend fun sample2() {
    //第 1 次拦截机会
    GlobalScope.launch(LogContinuationInterceptor()) {
        log(1)
        //第 2 次拦截机会
        val job = async {
            log(2)
            //第 3 次拦截机会
            delay(1000)
            log(3)
            "Hello"
        }
        log(4)
        //第 4 次拦截机会
        val result = job.await()
        log("5. $result")
    }.join()
    log(6)
}

private class LogContinuationInterceptor : ContinuationInterceptor {

    override val key = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>) = LogContinuation(continuation)

    private class LogContinuation<T>(private val original: Continuation<T>) : Continuation<T> {

        override val context = original.context

        override fun resumeWith(result: Result<T>) {
            log("<LogContinuation> $result")
            original.resumeWith(result)
        }

    }

}