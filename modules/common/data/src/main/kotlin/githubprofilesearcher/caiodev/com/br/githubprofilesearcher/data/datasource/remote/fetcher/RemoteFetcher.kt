package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.remote.fetcher

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ErrorState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.MaximumResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SuccessState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.UnknownHost
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultInteger
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteFetcher {
    suspend inline fun <reified T> call(call: () -> HttpResponse): State<*> =
        runCatching { call() }
            .fold(
                onSuccess = { content ->
                    if (content.status.isSuccess()) {
                        handleSuccess(content.headers, content.body<T>())
                    } else {
                        handleHttpError(content.status.value)
                    }
                },
                onFailure = { exception ->
                    handleException(exception)
                },
            )

    @PublishedApi
    internal inline fun <reified T> handleSuccess(
        headers: Headers,
        response: T?,
    ): SuccessState<T?> = Success(response, obtainTotalPages(headers))

    @PublishedApi
    internal fun handleHttpError(code: Int): ErrorState =
        when (code) {
            in ClientSideError -> ClientSide
            SearchQuotaReachedError -> SearchQuotaReached
            MaximumResultLimitReachedError -> MaximumResultLimitReached
            in ServerSideError -> ServerSide
            else -> Generic
        }

    @PublishedApi
    internal fun handleException(exception: Throwable): ErrorState =
        when (exception) {
            is ConnectException -> Connect
            is SocketTimeoutException -> SocketTimeout
            is SSLHandshakeException -> SSLHandshake
            is UnknownHostException -> UnknownHost
            else -> Generic
        }

    @PublishedApi
    internal fun obtainTotalPages(headers: Headers): Int {
        val header = headers[HEADER_NAME]
        if (!header.isNullOrEmpty()) {
            return Regex(headerPattern)
                .findAll(header)
                .map(MatchResult::value)
                .toList()[HEADER_LIST_INDEX]
                .toInt()
        }
        return defaultInteger()
    }

    private companion object {
        const val HEADER_NAME = "link"
        val headerPattern = "\\d+".toPattern().toString()
        const val HEADER_LIST_INDEX = 2
        val ClientSideError = listOf(400, 401, 402, 404)
        const val SearchQuotaReachedError = 403
        const val MaximumResultLimitReachedError = 422
        val ServerSideError = 500..599
    }
}
