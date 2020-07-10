package retrofit

import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface GitHubRetrofit {

    @GET("/")
    fun contributorsAsync(): Deferred<Map<String, String>>

    @GET("/")
    suspend fun mock1(): MockResult<String>?

    @GET("/")
    suspend fun mock2(): Response<MockResult<String>>

    @GET("/")
    fun mock3(): Single<MockResult<String>?>

    @GET("/")
    fun mock4(): Flowable<MockResult<String>?>
}

class MockResult<T>(
        val data: T,
        val status: Int,
        val message: String
)