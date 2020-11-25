package core.flow

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/9/30 15:39
 */
suspend fun main() {
    flow {
        for (i in 1..10) {
            emit(i)
        }
    }.map {
        it * it
    }.collect {
        println("result $it")
    }

}