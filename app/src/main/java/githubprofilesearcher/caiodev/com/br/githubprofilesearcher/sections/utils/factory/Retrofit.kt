package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.mediaType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retrofitTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.timberTag
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Retrofit {

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag(timberTag).d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @UnstableDefault
    @PublishedApi
    internal inline fun <reified T> provideRetrofitService(baseUrl: String): T =
        createRetrofitService(baseUrl)

    @UnstableDefault
    @PublishedApi
    internal inline fun <reified T> createRetrofitService(baseUrl: String) =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addConverterFactory(
                Json(JsonConfiguration(ignoreUnknownKeys = true)).asConverterFactory(
                    mediaType
                )
            )
            .build().create(T::class.java) as T

    @PublishedApi
    internal fun provideOkHttpClient(): OkHttpClient = createOkHttpClient()

    private fun createOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(retrofitTimeout, TimeUnit.SECONDS)
            .readTimeout(retrofitTimeout, TimeUnit.SECONDS)
            .writeTimeout(retrofitTimeout, TimeUnit.SECONDS)
            .build()
}