package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.BuildConfig
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

    private const val timeout = 60L

    @ExperimentalSerializationApi
    fun createAPIConnectorInstance(baseUrl: String = BuildConfig.API_URL): Retrofit {
        val mediaType = "application/json".toMediaType()
        return castValue(
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createLoggerClient())
                .addConverterFactory(
                    Json { ignoreUnknownKeys = true }.asConverterFactory(mediaType)
                )
                .build()
        )
    }

    @Suppress("UNUSED")
    fun createLoggerClient(): OkHttpClient {
        val responseTag = "OkHttp"
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Timber.tag(responseTag).d(message) }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
    }
}
