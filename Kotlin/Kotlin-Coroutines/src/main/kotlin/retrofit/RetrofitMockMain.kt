package retrofit

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/1/10 16:23
 */
private val okHttpClient = OkHttpClient.Builder().build()

private val retrofit = Retrofit.Builder().apply {
    baseUrl("http://" + MockServer.ROOT + ":" + MockServer.PORT)
    client(okHttpClient)
    addConverterFactory(ErrorJsonLenientConverterFactory(GsonConverterFactory.create()))
    addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
}.build()

private val githubApi = retrofit.create(GitHubRetrofit::class.java)

fun main() = sample2()

/*  如果是 RxJava 处理 Retrofit 结果，那么如果服务器 body 返回空，对于 Flowable 来说，只会调用 onCompleted，此时可以使用 compose 和 switchIfEmpty API。 */
fun sample4() {
    githubApi.mock4()
            .compose {
                it.switchIfEmpty(Flowable.error(NoSuchElementException())).map { it.data }
            }
            .subscribe(
                    {
                        println("result = $it")
                    },
                    {
                        println("error = $it")
                    },
                    {
                        println("completed")
                    })

    Thread.sleep(3000)
}

/* 如果是 RxJava 处理 Retrofit 结果，那么如果服务器 body 返回空，对于 Signle 来说，会调用 onError，异常为 NoSuchElementException。*/
fun sample3() {
    githubApi.mock3()
            .subscribe(
                    {
                        println("result = $it")
                    },
                    {
                        println("error = $it")
                    })

    Thread.sleep(3000)
}

/*目前来看，retrofit 接口中的 suspend 方法不支持返回 T?，返回注诸如 204 之类响应将会导致 kotlin.KotlinNullPointerException: Response from xxx was null but response body type was declared as non-null KotlinNullPointerException 异常*/
private fun sample2() = runBlocking {
    try {
        val mock2 = githubApi.mock1()
        if (mock2 == null) {
            println("body is null")
        } else {
            println(mock2)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

private fun sample1() = runBlocking {
    try {
        val mock2 = githubApi.mock2()
        println("mock2 is success: " + mock2.isSuccessful)
        if (mock2.body() == null) {
            println("body is null")
        } else {
            println(mock2)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}