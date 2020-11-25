package com.bennyhuo.kotlin.coroutines.core

import com.bennyhuo.kotlin.coroutines.*
import com.bennyhuo.kotlin.coroutines.cancel.suspendCancellableCoroutine
import com.bennyhuo.kotlin.coroutines.context.CoroutineName
import com.bennyhuo.kotlin.coroutines.scope.CoroutineScope
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

/**协程的初步实现*/
abstract class AbstractCoroutine<T>(context: CoroutineContext) :
        Job,
        Continuation<T>,
        /**AbstractCoroutine类实现了Continuation接口，为了监听协程完成的事件而作为completion参数在启动时传入。*/
        CoroutineScope {

    /**用户保证状态变更的原子性*/
    protected val state = AtomicReference<CoroutineState>()

    override val context: CoroutineContext

    override val scopeContext: CoroutineContext
        get() = context

    protected val parentJob = context[Job]

    private var parentCancelDisposable: Disposable? = null

    init {
        //状态被设置为Incomplete
        state.set(CoroutineState.InComplete())
        //作为Job的实现自身也被添加到协程上下文中，方便协程体内部以及其他逻辑获取。
        this.context = context + this

        parentCancelDisposable = parentJob?.invokeOnCancel {
            cancel()
        }
    }

    val isCompleted
        get() = state.get() is CoroutineState.Complete<*>

    override val isActive: Boolean
        get() = when (state.get()) {
            is CoroutineState.Complete<*>,
            is CoroutineState.Cancelling -> false
            else -> true
        }

    /**既然要把AbstractCoroutine当作协程的completion，那么resumeWith自然会在协程执行完成后调用，此时只需要将协程流转为完成状态，并通知此前注册的回调即可*/
    override fun resumeWith(result: Result<T>) {
        val newState = state.updateAndGet { prevState ->
            when (prevState) {
                //although cancelled, flows of job may work out with the normal result.
                //协程被取消后并不会立即停止执行，而是要等待内部的挂起点响应，因此这里从Cancelling流转到Complete是合理的。
                is CoroutineState.Cancelling,
                is CoroutineState.InComplete -> {
                    CoroutineState.Complete(result.getOrNull(), result.exceptionOrNull()).from(prevState)
                }
                is CoroutineState.Complete<*> -> {
                    throw IllegalStateException("Already completed!")
                }
            }
        }

        //newState一定为Complete，否则此处会直接抛出非法异常，因此我们无须做类型判断，此时调用它的notifyCompletion通知回调即可
        (newState as CoroutineState.Complete<T>).exception?.let(this::tryHandleException)

        newState.notifyCompletion(result)
        newState.clear()
        parentCancelDisposable?.dispose()
    }

    /**
     * join 的三种情况：
     *
     * 1. 被等待的协程已经完成，join不会挂起而是立即返回。
     * 2. 被等待的协程尚未完成，join立即挂起，直到协程完成。
     * 3. join本身也是一个挂起函数，因此必须在其他协程中调用，如果它所在的协程（注意，并非被等待的协程）取消，那么join会立即抛出CancellationException来响应取消。
     */
    override suspend fun join() {
        when (state.get()) {
            is CoroutineState.InComplete,
            is CoroutineState.Cancelling -> return joinSuspend()
            is CoroutineState.Complete<*> -> {
                val currentCallingJobState = coroutineContext[Job]?.isActive ?: return
                if (!currentCallingJobState) {
                    throw CancellationException("Coroutine is cancelled.")
                }
                return
            }
        }
    }

    private suspend fun joinSuspend() = suspendCancellableCoroutine<Unit> { continuation ->
        val disposable = doOnCompleted { result ->
            continuation.resume(Unit)
        }
        continuation.invokeOnCancellation { disposable.dispose() }
    }

    /**
     * 我们在这里调用了state.getAndUpdate来流转状态，返回值实际上是旧状态，旧状态如果是Incomplete，
     * 那么这次调用一定是发生了状态流转的情况，调用notifyCancellation来通知取消事件。
     */
    override fun cancel() {
        val prevState = state.getAndUpdate { prev ->
            when (prev) {
                is CoroutineState.InComplete -> {
                    CoroutineState.Cancelling().from(prev)
                }
                is CoroutineState.Cancelling,
                is CoroutineState.Complete<*> -> prev
            }
        }

        if (prevState is CoroutineState.InComplete) {
            prevState.notifyCancellation()
        }
        parentCancelDisposable?.dispose()
    }

    /**
     * 除Complete之外，其他两种状态的流转均构造了新的对象实例来确保并发安全，注册回调的过程分为以下三步：
     *
     * 1. 构造一个CompletionHandlerDisposable对象。它有一个dispose函数，调用时可以将对应的回调移除。
     * 2. 检查状态，并将回调添加到状态中。添加回调时不直接在原状态对象上直接修改，而是创建了新的状态对象，避免了并发安全的问题。
     * 3. 在状态流转成功后，可以获得最终的状态，如果此时已经是已完成的状态，这表明新回调没有注册到状态中，因此需要立即调用该回调。
     *
     * 如果需要移除注册的回调，只需要调用返回的CompletionHandlerDisposable对象的dispose函数即可
     */
    protected fun doOnCompleted(block: (Result<T>) -> Unit): Disposable {
        //1
        val disposable = CompletionHandlerDisposable(this, block)
        //2
        val newState = state.updateAndGet { prev ->
            when (prev) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(prev).with(disposable)
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling().from(prev).with(disposable)
                }
                is CoroutineState.Complete<*> -> {
                    prev
                }
            }
        }
        //3
        (newState as? CoroutineState.Complete<T>)?.let {
            block(
                    when {
                        it.exception != null -> Result.failure(it.exception)
                        it.value != null -> Result.success(it.value)
                        else -> throw IllegalStateException("Won't happen.")
                    }
            )
        }
        return disposable
    }

    override fun invokeOnCancel(onCancel: OnCancel): Disposable {
        val disposable = CancellationHandlerDisposable(this, onCancel)
        val newState = state.updateAndGet { prev ->
            when (prev) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(prev).with(disposable)
                }
                is CoroutineState.Cancelling,
                is CoroutineState.Complete<*> -> {
                    prev
                }
            }
        }
        (newState as? CoroutineState.Cancelling)?.let {
            // call immediately when Cancelling.
            onCancel()
        }
        return disposable
    }

    override fun invokeOnCompletion(onComplete: OnComplete): Disposable {
        return doOnCompleted { _ -> onComplete() }
    }

    override fun remove(disposable: Disposable) {
        state.updateAndGet { prev ->
            when (prev) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(prev).without(disposable)
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling().from(prev).without(disposable)
                }
                is CoroutineState.Complete<*> -> {
                    prev
                }
            }
        }
    }

    private fun tryHandleException(e: Throwable): Boolean {
        return when (e) {
            is CancellationException -> {
                false
            }
            else -> {
                (parentJob as? AbstractCoroutine<*>)?.handleChildException(e)?.takeIf { it }
                        ?: handleJobException(e)
            }
        }
    }

    protected open fun handleChildException(e: Throwable): Boolean {
        cancel()
        return tryHandleException(e)
    }

    protected open fun handleJobException(e: Throwable) = false

    override fun toString(): String {
        return context[CoroutineName].toString()
    }

}