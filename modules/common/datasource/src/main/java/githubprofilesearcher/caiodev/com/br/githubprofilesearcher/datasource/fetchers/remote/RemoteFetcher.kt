package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.UnknownHost
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteFetcher {
    suspend inline fun <reified T> call(call: () -> HttpResponse): State<*> {
        return try {
            val wrapper = call()
            val response = wrapper.body<T>()
            if (wrapper.status.isSuccess()) {
                handleSuccess(wrapper.headers, response)
            } else {
                handleHttpError(wrapper.status.value)
            }
        } catch (exception: IOException) {
            handleException(exception)
        }
    }

    @PublishedApi internal inline fun <reified T> handleSuccess(
        headers: Headers,
        response: T?,
    ): State<Success> {
        response?.let { apiResponse ->
            return SuccessWithBody(apiResponse, obtainTotalPages(headers))
        } ?: run {
            return SuccessWithoutBody
        }
    }

    @PublishedApi internal fun handleHttpError(responseCode: Int): State<Error> {
        return when (responseCode) {
            in ClientSideError -> ClientSide
            SEARCH_QUOTA_REACHED_ERROR -> SearchQuotaReached
            RESULT_LIMIT_REACHED_ERROR -> ResultLimitReached
            in ServerSideError -> ServerSide
            else -> Generic
        }
    }

    @PublishedApi internal fun handleException(exception: IOException): State<Error> {
        return when (exception) {
            is ConnectException -> Connect
            is SocketTimeoutException -> SocketTimeout
            is SSLHandshakeException -> SSLHandshake
            is UnknownHostException -> UnknownHost
            else -> Generic
        }
    }

    @PublishedApi internal fun obtainTotalPages(headers: Headers): Int {
        var totalPages = 0
        headers[HEADER_NAME]?.let { header ->
            if (header.isNotEmpty()) {
                totalPages =
                    Regex(headerPattern).findAll(header)
                        .map(MatchResult::value)
                        .toList()[HEADER_LIST_INDEX].toInt()
            }
        }
        return totalPages
    }

    private companion object {
        const val HEADER_NAME = "link"
        val headerPattern = "\\d+".toPattern().toString()
        const val HEADER_LIST_INDEX = 2
        val ClientSideError = 400..404
        const val SEARCH_QUOTA_REACHED_ERROR = 422
        const val RESULT_LIMIT_REACHED_ERROR = 451
        val ServerSideError = 500..511
    }
}
