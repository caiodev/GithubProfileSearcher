package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.api.factory

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import timber.log.Timber
import java.util.concurrent.TimeUnit

object APITestService {
    lateinit var mockWebServer: MockWebServer
    val json = Json { ignoreUnknownKeys = true }
    val mediaType = "application/json".toMediaType()

    fun setup(): MockWebServer {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        return mockWebServer
    }

    @ExperimentalSerializationApi
    inline fun <reified T> newInstance() =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(createLoggerClient())
            .addConverterFactory(
                json.asConverterFactory(
                    mediaType,
                ),
            )
            .build()
            .create(T::class.java) as T

    fun createLoggerClient() =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build()
}
