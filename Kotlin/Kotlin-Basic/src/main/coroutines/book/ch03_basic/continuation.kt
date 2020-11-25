package book.ch03_basic

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/9/29 14:17
 */
suspend fun notSuspend() = suspendCoroutine<Int>{ continuation ->
    continuation.resume(100)
}