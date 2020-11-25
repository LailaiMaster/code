package com.bennyhuo.kotlin.coroutines.core

/**对于协程来讲，启动之后主要就是未完成、已取消、已完成这几种状态*/
sealed class CoroutineState {

    /**用于存放注册后的回调的数据结构*/
    private var disposableList: DisposableList = DisposableList.Nil

    fun from(state: CoroutineState): CoroutineState {
        this.disposableList = state.disposableList
        return this
    }

    fun with(disposable: Disposable): CoroutineState {
        this.disposableList = DisposableList.Cons(disposable, this.disposableList)
        return this
    }

    fun without(disposable: Disposable): CoroutineState {
        this.disposableList = this.disposableList.remove(disposable)
        return this
    }

    fun <T> notifyCompletion(result: Result<T>) {
        this.disposableList.loopOn<CompletionHandlerDisposable<T>> {
            it.onComplete(result)
        }
    }

    fun notifyCancellation() {
        disposableList.loopOn<CancellationHandlerDisposable> {
            it.onCancel()
        }
    }

    fun clear() {
        this.disposableList = DisposableList.Nil
    }

    override fun toString(): String {
        return "CoroutineState.${this.javaClass.simpleName}"
    }

    /**Incomplete：协程启动后立即进入该状态，直到完成或者被取消。*/
    class InComplete : CoroutineState()
    /**Cancelling：协程执行中被取消后进入该状态。*/
    class Cancelling : CoroutineState()
    /**Complete：协程执行完成（包括正常返回和异常结束）时进入该状态。*/
    class Complete<T>(val value: T? = null, val exception: Throwable? = null) : CoroutineState()
}