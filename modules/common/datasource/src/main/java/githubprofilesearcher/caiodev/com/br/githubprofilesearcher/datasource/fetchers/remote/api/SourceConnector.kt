package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api

import com.chuckerteam.chucker.api.ChuckerInterceptor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.scope.Scope

object SourceConnector {
    private const val TIMEOUT = 60_000L

    fun Scope.newInstance(
        baseUrl: String = BuildConfig.BASE_URL,
        interceptor: ChuckerInterceptor,
    ): HttpClient {
        return HttpClient(OkHttp) {
            defaultRequest { url(baseUrl) }
            engine { addInterceptor(interceptor) }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(HttpTimeout) {
                connectTimeoutMillis = TIMEOUT
                requestTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.ALL
                }
            }
        }
    }
}
