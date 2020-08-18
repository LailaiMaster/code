package retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private val okHttpClient = OkHttpClient.Builder().build()

private val retrofit = Retrofit.Builder().apply {
    baseUrl("https://api.github.com")
    client(okHttpClient)
    addConverterFactory(GsonConverterFactory.create())
    addCallAdapterFactory(CoroutineCallAdapterFactory())
}.build()

private val githubApi = retrofit.create(GitHubRetrofit::class.java)

private fun sample1() = runBlocking {

    println("Making GitHub API request")

    val result = githubApi.contributorsAsync()

    launch {
        try {
            val contributors = result.await()
            println("contributors = $contributors")
        } finally {
            okHttpClient.dispatcher.executorService.shutdown()
        }
    }

    println("end...")
    delay(100)
    //result.cancel()
}


fun main() {
    sample1()
}
