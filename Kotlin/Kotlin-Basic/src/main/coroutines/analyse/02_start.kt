package analyse

import kotlinx.coroutines.CoroutineStart.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2019/8/25 11:09
 */
@ExperimentalCoroutinesApi
suspend fun main() {
    //sample1() //线程
    //sample2()
    //sample3()
    //sample4()
    sample5()
}

@ExperimentalCoroutinesApi
private suspend fun sample5() {
    log(1)
    val job = GlobalScope.launch(start = UNDISPATCHED) {
        log(2)
        delay(100)
        log(3)
    }
    log(4)
    job.join()
    log(5)
}

@ExperimentalCoroutinesApi
private suspend fun sample4() {
    log(1)
    val job = GlobalScope.launch(start = ATOMIC) {
        log(2)
    }
    job.cancel()
    log(3)

}

private suspend fun sample3() {
    log(1)
    val job = GlobalScope.launch(start = LAZY) {
        log(2)
    }
    log(3)
    job.join()
    log(4)
}

private suspend fun sample2() {
    log(1)
    val job = GlobalScope.launch {
        log(2)
    }
    log(3)
    job.join()
    log(4)
}

private fun sample1() {
    thread {
        log("Hello")
    }
}

