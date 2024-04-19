package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ErrorState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SuccessState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.UnknownHost
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
                onFailure = { exception -> handleException(exception) }
            )

    @PublishedApi
    internal inline fun <reified T> handleSuccess(
        headers: Headers,
        response: T?,
    ): SuccessState<T?> = Success(response, obtainTotalPages(headers))

    @PublishedApi
    internal fun handleHttpError(responseCode: Int): ErrorState {
        return when (responseCode) {
            in ClientSideError -> ClientSide
            SearchQuotaReachedError -> SearchQuotaReached
            ResultLimitReachedError -> ResultLimitReached
            in ServerSideError -> ServerSide
            else -> Generic
        }
    }

    @PublishedApi
    internal fun handleException(exception: Throwable): ErrorState {
        return when (exception) {
            is ConnectException -> Connect
            is SocketTimeoutException -> SocketTimeout
            is SSLHandshakeException -> SSLHandshake
            is UnknownHostException -> UnknownHost
            else -> Generic
        }
    }

    @PublishedApi
    internal fun obtainTotalPages(headers: Headers): Int {
        var totalPages = defaultInteger()
        val header = headers[HEADER_NAME]
        if (!header.isNullOrEmpty()) {
            totalPages =
                Regex(headerPattern)
                    .findAll(header)
                    .map(MatchResult::value)
                    .toList()[HEADER_LIST_INDEX]
                    .toInt()
        }
        return totalPages
    }

    private companion object {
        const val HEADER_NAME = "link"
        val headerPattern = "\\d+".toPattern().toString()
        const val HEADER_LIST_INDEX = 2
        val ClientSideError = 400..404
        val SearchQuotaReachedError = 422
        val ResultLimitReachedError = 451
        val ServerSideError = 500..511
    }
}
