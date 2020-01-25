package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.factory

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object RetrofitTestService {

    lateinit var mockWebServer: MockWebServer

    fun setup(): MockWebServer {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        return mockWebServer
    }

    @UnstableDefault
    @PublishedApi
    internal inline fun <reified T> createRetrofitService() =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(createOkHttpClient())
            .addConverterFactory(
                Json(JsonConfiguration(strictMode = false)).asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build().create(T::class.java) as T

    @PublishedApi
    internal fun createOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build()
}