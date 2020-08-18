package core.exception_handing

import kotlinx.coroutines.*

/*
协程构建器有两种风格：自动的传播异常（launch 以及 actor） 或者将它们暴露给用户（async 以及 produce）。
    前者对待异常是不处理的，类似于 Java 的 Thread.uncaughtExceptionHandler。
    后者依赖用户来最终消耗异常，比如说，通过 await 或 receive 。
 */
fun main() = runBlocking {
    val job = GlobalScope.launch {
        delay(1000)
        println("Throwing exception from launch")
        //我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
        throw IndexOutOfBoundsException() // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
    }

    try {
        //launch 的异常不会被处理
        job.join()
        println(message = "after joining")
    } catch (e: Exception) {
        //不会走这里
        e.printStackTrace()
    }

    println("Joined failed job")
    val deferred = GlobalScope.async {
        delay(1000)
        println("Throwing exception from async")
        //没有打印任何东西，依赖用户去调用等待
        throw ArithmeticException() // Nothing is printed, relying on user to call await
    }

    try {
        deferred.await()
        println(message = "Unreached")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException")
    }
}