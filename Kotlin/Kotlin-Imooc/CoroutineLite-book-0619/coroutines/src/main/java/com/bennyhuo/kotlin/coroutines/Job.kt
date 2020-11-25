package com.bennyhuo.kotlin.coroutines

import com.bennyhuo.kotlin.coroutines.core.Disposable
import kotlin.coroutines.CoroutineContext

/**如果我们想知道它什么时候结束，可以通过注册OnComplete回调来做到这一点。*/
typealias OnComplete = () -> Unit

typealias CancellationException = java.util.concurrent.CancellationException

typealias OnCancel = () -> Unit

/**

1. 降低协程的创建成本无非就是提供一个函数来简化操作，就像 `async{...}` 函数那样；而要降低管理的成本，就必须引入一个新的类型来描述协程本身，并且提供相应的 API 来控制协程的执行。
2. 降低协程的管理成本，则需要对协程进行封装，让它的状态管理更加简便，Job 就是一个用于描述协程的类，其部分 API 与线程类似。。
 */
interface Job : CoroutineContext.Element {

    /**用于在上下文容器中查找 Job*/
    companion object Key : CoroutineContext.Key<Job>

    override val key: CoroutineContext.Key<*> get() = Job

    val isActive: Boolean

    fun invokeOnCancel(onCancel: OnCancel): Disposable

    fun invokeOnCompletion(onComplete: OnComplete): Disposable

    fun cancel()

    fun remove(disposable: Disposable)

    suspend fun join()

}