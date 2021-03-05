package practice

import io.reactivex.Flowable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import utils.RxLock
import java.io.IOException
import java.util.*

/** returning true means accepting the remote data */
typealias Selector<T> = (local: T, remote: T?) -> Boolean

/** invoke when new data receiving */
typealias Consumer<T> = (newData: T?) -> Unit

data class CombinedResult<T>(
        val dataType: DataType,
        val data: T?,
        val error: Throwable?
)

enum class DataType {
    Remote, Disk
}

var connected = true

fun connected(): Boolean {
    return connected
}

class NetworkErrorException : Exception()
class ApiErrorException(val code: Int, message: String?) : Exception(message)
class HttpException : Exception()

/**
 * 1. 如果网络不可用，直接返回缓存，如果没有缓存，报错没有网络连接。
 * 2. 如果存在网络：
 *      - 如果没有缓存，则从网络获取，此时网络加载发生错误将会被忽略
 *      - 如果有缓存，则先返回缓存，然后从网络获取。
 *      - 对比缓存与网络数据，如果没有更新，则忽略。
 *      - 如果有更新，则更新缓存，并返回网络数据。
 *
 * @param remote    网络数据源。
 * @param local     本地数据源。
 * @param onNewData 当有更新时，返回新的数据，可以在这里进行存储操作。
 * @param <T>       数据类型。
 * @param selector  比较器，返回当 true 表示两者相等，如果相等，则 remote 数据将会被忽略。
 * @return 组合后的Observable
 */
fun <T> combineRemoteAndLocalSimultaneously(
        remote: Flowable<Optional<T>>,
        local: Flowable<Optional<T>>,
        selector: Selector<T?>,
        onNewData: Consumer<T>
): Flowable<Optional<out T>> {

    //没有网络
    if (!connected()) {
        return local.flatMap {
            if (it.isPresent) {
                Flowable.just(it)
            } else {
                Flowable.error(NetworkErrorException())
            }
        }
    }

    //有网络
    val sharedLocal = local.defaultIfEmpty(Optional.empty()).replay()
    sharedLocal.connect()

    val filteredRemote = remote.onErrorResumeNext(Function {
        if (isNetworkError(it)) {
            Flowable.never()
        } else {
            if (it is ApiErrorException) {
                onNewData(null)
            }
            Flowable.error(it)
        }
    }).flatMap { remoteData ->
        sharedLocal.flatMap { localData ->
            if (!localData.isPresent || selector(localData.get(), remoteData.orElse(null))) {
                Flowable.just(remoteData)
            } else {
                Flowable.empty()
            }
        }
    }.doOnNext { newData: Optional<T> ->
        onNewData(newData.orElse(null))
    }


    return Flowable.merge(sharedLocal.filter { obj: Optional<T> -> obj.isPresent }, filteredRemote)
}


/**
 * 该方式，始终能得到错误通知。
 *
 * 1. 如果网络不可用，直接返回缓存，如果没有缓存，报错没有网络连接。
 * 2. 如果存在网络：
 *      - 如果没有缓存，则从网络获取，此时网络加载发生错误将会被忽略
 *      - 如果有缓存，则先返回缓存，然后从网络获取。
 *      - 对比缓存与网络数据，如果没有更新，则忽略。
 *      - 如果有更新，则更新缓存，并返回网络数据。
 *
 * @param remote    网络数据源。
 * @param local     本地数据源。
 * @param onNewData 当有更新时，返回新的数据，可以在这里进行存储操作。
 * @param <T>       数据类型。
 * @param selector  比较器，返回当 true 表示两者相等，如果相等，则 remote 数据将会被忽略。
 * @return 组合后的Observable
 */
fun <T> combineRemoteAndLocalReturningErrorSimultaneously(
        remote: Flowable<Optional<T>>,
        local: Flowable<Optional<T>>,
        delayNetError: Boolean = false,
        selector: Selector<T>,
        onNewData: Consumer<T>
): Flowable<CombinedResult<out T?>> {

    val sharedLocal = local.defaultIfEmpty(Optional.empty()).replay()
    sharedLocal.connect()

    //组合数据
    val complexRemote = remote.flatMap { remoteData ->
        sharedLocal.flatMap { localData ->
            if (!localData.isPresent || selector(localData.get(), remoteData.orElse(null))) {
                Flowable.just(remoteData)
            } else {
                Flowable.empty()
            }
        }
    }.doOnNext { newData: Optional<T> ->
        onNewData(newData.orElse(null))
    }.map {
        CombinedResult<T?>(DataType.Remote, it.orElse(null), null)
    }.onErrorResumeNext(Function { throwable ->
        if (throwable is ApiErrorException) {
            onNewData(null)
        }
        if (delayNetError) {
            sharedLocal.flatMap {
                Flowable.just(CombinedResult<T?>(DataType.Remote, null, throwable))
            }
        } else {
            Flowable.just(CombinedResult<T?>(DataType.Remote, null, throwable))
        }
    })

    val mappedLocal: Flowable<CombinedResult<T>> = sharedLocal
            .filter { obj: Optional<T> -> obj.isPresent }
            .map {
                CombinedResult<T>(DataType.Disk, it.get(), null)
            }

    return Flowable.merge(mappedLocal, complexRemote)
}


private fun isNetworkError(exception: Throwable): Boolean {
    return exception is IOException || exception is HttpException || exception is NetworkErrorException
}

fun <T> combineRemoteAndLocal(
        remote: Flowable<Optional<T>>,
        local: Flowable<Optional<T>>,
        onNewData: (T?) -> Unit
): Flowable<Optional<out T>> {
    return combineRemoteAndLocalSimultaneously(remote, local, { t1, t2 -> t1 != t2 }, onNewData)
}

fun <T> combineRemoteAndLocalReturningError(
        remote: Flowable<Optional<T>>,
        local: Flowable<Optional<T>>,
        delayNetError: Boolean,
        onNewData: (T?) -> Unit
): Flowable<CombinedResult<out T?>> {
    return combineRemoteAndLocalReturningErrorSimultaneously(remote, local, delayNetError, { t1, t2 -> t1 != t2 }, onNewData)
}

/**
 * 演示组合网络结果
 */
fun main() {
//    testCombineRemoteAndLocal()
    testCombineRemoteAndLocalReturningError()
}

fun testCombineRemoteAndLocal() {
    connected = true

    val local = Flowable.fromCallable<Optional<String>> {
        println("local start loading: ${Thread.currentThread()}")
        RxLock.sleep(500)
        println("local finished loading")
        Optional.of("Local")
//        Optional.empty()
    }.subscribeOn(Schedulers.io())

    val remote = Flowable.fromCallable<Optional<String>> {
        println("remote start loading ${Thread.currentThread()}")
        RxLock.sleep(3000)
        println("remote finished loading ${Thread.currentThread()}")
//        throw HttpException()
        Optional.of("Remote")
    }

    combineRemoteAndLocalSimultaneously(remote, local, { d1, d2 -> d1 != d2 }) {

    }.subscribe {
        println("----------------result ${it.get()}")
    }

    RxLock.lock()
}

fun testCombineRemoteAndLocalReturningError() {
    connected = true

    val local = Flowable.fromCallable<Optional<String>> {
        println("local start loading: ${Thread.currentThread()}")
        RxLock.sleep(300)
        println("local finished loading")
        Optional.of("Local")
    }.subscribeOn(Schedulers.io())

    val remote = Flowable.fromCallable<Optional<String>> {
        println("remote start loading ${Thread.currentThread()}")
        RxLock.sleep(1000)
        println("remote finished loading ${Thread.currentThread()}")
        throw HttpException()
    }

    combineRemoteAndLocalReturningErrorSimultaneously(remote, local, true, { d1, d2 -> d1 != d2 }) {

    }.subscribe {
        println("----------------result $it")
    }

    RxLock.sleep(2000)
}
