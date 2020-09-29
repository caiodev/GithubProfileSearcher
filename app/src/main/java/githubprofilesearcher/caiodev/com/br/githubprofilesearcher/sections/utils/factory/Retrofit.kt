package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.mediaType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.responseTag
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.timeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Retrofit {

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Timber.tag(responseTag).d(message) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @ExperimentalSerializationApi
    @PublishedApi
    internal inline fun <reified T> provideRetrofitService(baseUrl: String): T =
        createRetrofitService(baseUrl)

    @ExperimentalSerializationApi
    @PublishedApi
    internal inline fun <reified T> createRetrofitService(baseUrl: String) =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory(mediaType)
            )
            .build().create(T::class.java) as T

    @PublishedApi
    internal fun provideOkHttpClient(): OkHttpClient = createOkHttpClient()

    private fun createOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
}
