package rxjava2

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/* https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/kotlinx-coroutines-rx-example/src/main.kt */
interface GitHubRx {
    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(
            @Path("owner") owner: String,
            @Path("repo") repo: String
    ): Observable<List<Contributor>>

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Single<List<Repo>>
}

data class Contributor(val login: String, val contributions: Int)

data class Repo(val name: String)