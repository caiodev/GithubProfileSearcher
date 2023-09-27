package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import timber.log.Timber

object SourceConnector {

    private const val baseUrl = "https://api.github.com/"
    private const val timeout = 60000L
    private const val responseTag = "OkHttp"

    fun Scope.newInstance(
        baseUrl: String = this@SourceConnector.baseUrl,
    ): HttpClient {

        return HttpClient(OkHttp) {

            defaultRequest { url(baseUrl) }

            engine {
                addInterceptor(
                    HttpLoggingInterceptor { message -> Timber.tag(responseTag).d(message) }
                        .apply { level = HttpLoggingInterceptor.Level.BODY }
                )
            }

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(HttpTimeout) {
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
        }
    }
}
