package analyse

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/3/21 19:04
 */
fun main() = runBlocking {

    val localDeferred = getUserFromLocal()
    val remoteDeferred = getUserFromApi()

    //两个 API 分别从网络和本地缓存获取数据，期望哪个先返回就先用哪个做展示
    val userResponse = select<Response<String?>> {
        localDeferred.onAwait {
            Response(it, true)
        }
        remoteDeferred.onAwait {
            Response(it, false)
        }
    }

    userResponse.value?.let { log(it) }
    //如果先返回的是本地缓存，那么我们还需要获取网络结果来展示最新结果
    userResponse.isLocal.takeIf { it }?.let {
        val userFromApi = remoteDeferred.await()
        //cacheUser(login, userFromApi)
        log(userFromApi)
    }


    Unit
}


class Response<T>(val value: T, val isLocal: Boolean)

fun CoroutineScope.getUserFromApi(login: String = "") = async(Dispatchers.IO) {
    delay(3000)
    "Data From API"
}

fun CoroutineScope.getUserFromLocal(login: String = "") = async(Dispatchers.IO) {
    delay(1000)
    "Data From Local"
}