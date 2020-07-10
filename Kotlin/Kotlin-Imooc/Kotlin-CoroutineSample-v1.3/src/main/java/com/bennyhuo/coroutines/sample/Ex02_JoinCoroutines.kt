package com.bennyhuo.coroutines.sample

import com.bennyhuo.coroutines.utils.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    log(1)
    val job = launch {
        log(-1)
        delay(1000L)
        log(-2)
    }
    log(2)
    job.join()
    log(3)
}