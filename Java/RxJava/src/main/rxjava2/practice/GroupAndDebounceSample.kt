package practice

import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/9/8 15:21
 */

data class PostVO(
        val id: String
)

private val likeProcessor: PublishProcessor<PostVO> = PublishProcessor.create()

fun main() {
    likeProcessor
            .groupBy { it.id }
            .map {
                it.debounce(600, TimeUnit.MILLISECONDS)
            }
            .observeOn(Schedulers.single())
            .subscribe {
              it.subscribe {
                  println(it)
              }
            }

    thread {
        repeat(10) {
            likeProcessor.onNext(PostVO((it % 2).toString()))
            Thread.sleep(300)
        }
    }

    Thread.sleep(8000)
}