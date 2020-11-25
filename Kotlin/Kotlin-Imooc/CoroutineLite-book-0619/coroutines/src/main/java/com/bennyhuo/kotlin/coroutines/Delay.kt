package com.bennyhuo.kotlin.coroutines

import com.bennyhuo.kotlin.coroutines.cancel.suspendCancellableCoroutine
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
协程中 delay 的好处是：

1. 不需要阻塞线程，因此不会浪费资源。
2. 是个挂起函数，指定时间之后能够恢复执行即可。

而在 Java 平台上，实现 delay 使用的是 ScheduledExecutorService，但是 ScheduledExecutorService 实现定时任务，其底层也是通过线程阻塞实现的，这不是对线程资源的浪费么？

1. 如果当前线程有特殊地位，例如 UI 相关平台的 UI 线程，或像 `Vert.x` 这样的事件循环所在的线程，那么这些线程是不能被阻塞的，因此切换到后台线程的阻塞是有意义的。
2. 后台一个线程可以承载非常多的延时任务，例如，有 10 个协程调用 delay，那么只需要阻塞一个后台线程就可以实现这 10 个协程的延时执行。通过这种方式实现的 delay 实际上提升了线程资源利用率。

 */
suspend fun delay(time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) {
    if (time <= 0) {
        return
    }

    suspendCancellableCoroutine<Unit> { continuation ->
        val future = executor.schedule({ continuation.resume(Unit) }, time, unit)
        continuation.invokeOnCancellation { future.cancel(true) }
    }
}

private val executor = Executors.newScheduledThreadPool(1) { runnable ->
    Thread(runnable, "Scheduler").apply { isDaemon = true }
}
