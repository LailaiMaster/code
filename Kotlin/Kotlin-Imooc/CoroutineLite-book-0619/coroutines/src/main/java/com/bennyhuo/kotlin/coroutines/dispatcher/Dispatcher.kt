package com.bennyhuo.kotlin.coroutines.dispatcher

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor

/**
协程需要调度的位置就是挂起点的位置。当协程执行到挂起点的位置时，如果产生异步行为，协程就会在这个挂起点挂起。

这里的异步情况可能包括以下几种形式：

 1. 挂起点对应的挂起函数内部切换了线程，并在该线程内部调用Continuation的恢复调用来恢复。
 2. 挂起函数内部通过某种事件循环的机制将Continuation的恢复调用转到新的线程调用栈上执行.
 3. 挂起函数内部将Continuation实例保存，在后续某个时机再执行恢复调用，这个过程中不一定发生线程切换，但函数调用栈会发生变化，例如序列生成器的实现。

只有当挂起点真正挂起，我们才有机会实现调度，而实现调度需要使用协程的拦截器。
 */
interface Dispatcher {
    fun dispatch(block: () -> Unit)
}

/**将调度器和拦截器结合起来，拦截器是协程上下文元素的一类实现*/
open class DispatcherContext(private val dispatcher: Dispatcher) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> = DispatchedContinuation(continuation, dispatcher)
}

private class DispatchedContinuation<T>(val delegate: Continuation<T>, val dispatcher: Dispatcher) : Continuation<T> {
    override val context = delegate.context

    override fun resumeWith(result: Result<T>) {
        dispatcher.dispatch {
            delegate.resumeWith(result)
        }
    }
}