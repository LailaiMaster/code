package book.ch03_basic

import kotlinx.coroutines.delay
import kotlin.coroutines.*

/**
 * 《深入理解Kotlin协程》chapter 3.1：协程的创建
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/9/25 10:08
 */

fun main() {
    //与协程的创建和启动相关的API一共有两组

    //协程体不带 Receiver
    createCoroutine()
    //startCoroutine()

    //协程体带 Receiver
    //callLaunchCoroutine()
}

///////////////////////////////////////////////////////////////////////////
// 协程体不带 Receiver
///////////////////////////////////////////////////////////////////////////

private fun createCoroutine() {
    /*
    createCoroutine函数：
        参数completion会在协程执行完成后调用，实际上就是协程的完成回调。
        返回值是一个Continuation对象，由于现在协程仅仅被创建出来，因此需要通过这个值在之后触发协程的启动。
     */
    val continuation = suspend {
        println("In coroutine")
        4
    }.createCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println("Coroutine End: $result")
        }
    })

    continuation.resume(Unit)
}

private fun startCoroutine() {
    //startCoroutine 仅仅是在 createCoroutine 基础上帮我们调用了 Resume 方法。
    suspend {
        println("In coroutine")
        4
    }.startCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println("Coroutine End: $result")
        }
    })
}

///////////////////////////////////////////////////////////////////////////
// 协程体带 Receiver
///////////////////////////////////////////////////////////////////////////

fun callLaunchCoroutine() {
    launchCoroutine(ProducerScope<Int>()) {
        println("In coroutine.")
        produce(1024)
        delay(1000)
        produce(2048)
    }

    launchCoroutine(RestrictsProducerScope<Int>()) {
        println("In coroutine.")
        produce(1024)
        //delay(1000)
        produce(2048)
    }

}

/**
 * Kotlin没有提供直接声明带有Receiver的Lambda表达式的语法，为了方便使用带有Receiver的协程API，
 * 我们封装一个用以启动协程的函数launchCoroutine
 *
 * 多了一个 Receiver 类型 R，可以为协程体提供一个作用域，在协程体内我们可以直接使用作用域内提供的函数或者状态等。
 */
fun <R, T> launchCoroutine(receiver: R, black: suspend R.() -> T) {
    black.startCoroutine(receiver, object : Continuation<T> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<T>) {
            println("Coroutine End: $result")
        }
    })
}

class ProducerScope<T> {
    suspend fun produce(value: T) {

    }
}

/*
作用域可以用来提供函数支持，自然也就可以用来增加限制。如果我们为Receiver对应的类型增加一个RestrictsSuspension注解，那么在它的作用下，协程体内就无法调用外部的挂起函数了。
*/
@RestrictsSuspension
class RestrictsProducerScope<T> {
    suspend fun produce(value: T) {

    }
}
