package book.ch04_practice.python

import kotlin.coroutines.*

interface Generator<T> {
    operator fun iterator(): Iterator<T>
}

interface GeneratorScope<T> {
    suspend fun yield(value: T)
}

class GeneratorImpl<T>(
        private val block: suspend GeneratorScope<T>.(T) -> Unit,
        private val parameter: T
) : Generator<T> {

    override fun iterator(): Iterator<T> {
        return GeneratorIterator(block, parameter)
    }

}

fun <T> generator(block: suspend GeneratorScope<T>.(T) -> Unit): (T) -> Generator<T> {
    return { parameter: T ->
        println("new GeneratorImpl with parameter: $parameter")
        GeneratorImpl(block, parameter)
    }
}

sealed class State {
    class NotReady(val continuation: Continuation<Unit>) : State()
    class Ready<T>(val continuation: Continuation<Unit>, val nextValue: T) : State()
    object Done : State()
}

class GeneratorIterator<T>(
        private val block: suspend GeneratorScope<T>.(T) -> Unit,
        private val parameter: T
) : GeneratorScope<T>, Iterator<T>, Continuation<Any?> {

    override val context: CoroutineContext = EmptyCoroutineContext

    private var state: State

    init {
        val coroutineBlock: suspend GeneratorScope<T>.() -> Unit =
                { block(parameter) }
        val start = coroutineBlock.createCoroutine(this, this)
        state = State.NotReady(start)
    }

    override suspend fun yield(value: T) {
        println("yield")
        suspendCoroutine<Unit> { continuation ->
            state = when (state) {
                is State.NotReady -> State.Ready(continuation, value)
                is State.Ready<*> -> throw IllegalStateException("Cannot yield a value while ready.")
                State.Done -> throw IllegalStateException("Cannot yield a value while done.")
            }
        }
    }

    private fun resume() {
        println("resume")
        when (val currentState = state) {
            //用于启动协程
            is State.NotReady -> {
                println("resume really")
                currentState.continuation.resume(Unit)
            }
        }
    }

    override fun hasNext(): Boolean {
        println("hasNext")
        resume()
        return state != State.Done
    }

    override fun next(): T {
        println("next")
        return when (val currentState = state) {
            is State.NotReady -> {
                resume()
                return next()
            }
            is State.Ready<*> -> {
                state = State.NotReady(currentState.continuation)
                (currentState as State.Ready<T>).nextValue
            }
            State.Done -> throw IndexOutOfBoundsException("No value left.")
        }
    }

    override fun resumeWith(result: Result<Any?>) {
        println("resumeWith $result")
        state = State.Done
        result.getOrThrow()
    }

}

/**
 * 用 kotlin 模式 python 的 generator，generator 的应用场景：生成器是一类特殊的迭代器，
 * 使用yield而不是return语句返回结果。yield语句一次返回一个结果，在每个结果中间，挂起函数的状态，以便下次从它离开的地方继续执行。
 */
fun main() {
    val nums = generator { start: Int ->
        for (i in 0..5) {
            yield(start + i)
        }
    }

    val seq = nums(10)

    for (j in seq) {
        println(j)
    }

    val sequence = sequence {
        yield(1)
        yield(2)
        yield(3)
        yield(4)
        yieldAll(listOf(1, 2, 3, 4))
    }

    for (element in sequence) {
        println(element)
    }

    val fibonacci = sequence {
        yield(1L) // first Fibonacci number
        var current = 1L
        var next = 1L
        while (true) {
            yield(next) // next Fibonacci number
            next += current
            current = next - current
        }
    }

    fibonacci.take(10).forEach(::println)
}