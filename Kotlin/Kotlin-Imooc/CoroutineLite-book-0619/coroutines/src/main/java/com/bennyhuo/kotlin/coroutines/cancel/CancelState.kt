package com.bennyhuo.kotlin.coroutines.cancel

import com.bennyhuo.kotlin.coroutines.OnCancel

/**
 * 取消状态的定义其实与协程的状态是一致的，不同之处在于CancellableContinuation的取消回调只允许注册一个，
 * 因此这里不需要像协程状态一样用递归列表来存储回调，只需要一个CancelHandler来存储即可。
 */
sealed class CancelState {
    override fun toString(): String {
        return "CancelState.${this.javaClass.simpleName}"
    }

    object InComplete : CancelState()
    class CancelHandler(val onCancel: OnCancel) : CancelState()
    class Complete<T>(val value: T? = null, val exception: Throwable? = null) : CancelState()
    object Cancelled : CancelState()
}