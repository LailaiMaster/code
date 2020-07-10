package com.bennyhuo.coroutines.sample

import com.bennyhuo.coroutines.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    log(1)
    GlobalScope.launch {
        log(-1)
        delay(1000L)
        log(-2)
    }
    log(2)
    Thread.sleep(5000L)
    log(3)
}