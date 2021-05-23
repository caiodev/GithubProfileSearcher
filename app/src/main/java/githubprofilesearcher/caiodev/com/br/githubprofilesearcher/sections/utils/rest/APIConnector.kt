package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object APIConnector {

    private val mediaType = "application/json".toMediaType()
    private const val baseUrl = "https://api.github.com/"
    private const val responseTag = "OkHttp"
    private const val timeout = 60L
    private val httpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Timber.tag(responseTag).d(message) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @ExperimentalSerializationApi
    internal inline fun <reified T> provideAPIConnector(baseUrl: String = this.baseUrl): T =
        createAPIService(baseUrl)

    @ExperimentalSerializationApi
    private inline fun <reified T> createAPIService(baseUrl: String) =
        castValue<T>(
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(provideLoggerClient())
                .addConverterFactory(
                    Json { ignoreUnknownKeys = true }.asConverterFactory(mediaType)
                )
                .build().create(T::class.java)
        )

    private fun provideLoggerClient() = createLoggerClient()

    private fun createLoggerClient() =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
}
