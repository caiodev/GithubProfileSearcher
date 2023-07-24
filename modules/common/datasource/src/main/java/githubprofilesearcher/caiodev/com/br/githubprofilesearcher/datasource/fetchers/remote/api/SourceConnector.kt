package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.BuildConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object SourceConnector {

    private const val timeout = 60L
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json".toMediaType()

    @ExperimentalSerializationApi
    fun Scope.newInstance(
        baseUrl: String = BuildConfig.API_URL,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createLoggerClient())
            .addConverterFactory(
                json.asConverterFactory(mediaType),
            )
            .build()
    }

    private fun createLoggerClient(): OkHttpClient {
        val responseTag = "OkHttp"
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Timber.tag(responseTag).d(message) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
    }
}
