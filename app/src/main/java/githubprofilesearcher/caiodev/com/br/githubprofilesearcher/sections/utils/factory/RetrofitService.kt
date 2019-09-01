package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RetrofitService {

    @PublishedApi
    internal val baseUrl = "https://api.github.com/"

    private val timberTag = "OkHttp"

    @PublishedApi
    internal var retrofitBuilder: Any? = null

    private var okHttpClient: OkHttpClient? = null

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag(timberTag).d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @PublishedApi
    internal inline fun <reified T> getRetrofitService(): T {
        retrofitBuilder?.let { retrofitService ->
            return retrofitService as T
        } ?: run {
            retrofitBuilder = createRetrofitService<T>()
            return retrofitBuilder as T
        }
    }

    @PublishedApi
    internal inline fun <reified T> createRetrofitService() =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(T::class.java) as T

    @PublishedApi
    internal fun getOkHttpClient(): OkHttpClient {
        okHttpClient?.let { client ->
            return client
        } ?: run {
            okHttpClient = createOkHttpClient()
            return okHttpClient as OkHttpClient
        }
    }

    private fun createOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
}