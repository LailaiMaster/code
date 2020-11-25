package com.bennyhuo.kotlin.coroutines

/**很多时候我们更想拿到协程的返回值，因此我们基于Job定义一个接口Deferred，
 *
 * 1. 在协程已经执行完成时，立即返回协程的结果；
 * 2. 如果协程异常结束，则抛出该异常。如果协程尚未完成，则挂起直到协程执行完成，这一点与join类似。
 */
interface Deferred<T> : Job {

    suspend fun await(): T

}