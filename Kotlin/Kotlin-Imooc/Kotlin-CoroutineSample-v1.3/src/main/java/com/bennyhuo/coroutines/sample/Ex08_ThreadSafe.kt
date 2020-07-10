package com.bennyhuo.coroutines.sample

import com.bennyhuo.coroutines.utils.log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


var foo = 0
//var foo = AtomicInteger(0)

val bar = 1

fun main(args: Array<String>) = runBlocking {
    List(1000) {
        launch {
            repeat(1000) {
                foo ++
            }
        }
    }.forEach {
        it.join()
    }
    log(foo)
}