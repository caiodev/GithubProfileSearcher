package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.remote

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
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepository {

    suspend fun <T> call(
        call: suspend () -> Response<T>,
    ): State<*> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                handleSuccess(response)
            } else {
                handleHttpError(response.code())
            }
        } catch (exception: IOException) {
            handleException(exception)
        }
    }

    private fun handleSuccess(response: Response<*>): State<Success> {
        response.body()?.let { apiResponse ->
            return SuccessWithBody(apiResponse, obtainTotalPages(response.headers()))
        } ?: run {
            return SuccessWithoutBody()
        }
    }

    private fun handleHttpError(responseCode: Int): State<Error> {
        return when (responseCode) {
            in ClientSideError -> ClientSide
            SearchQuotaReachedError -> SearchQuotaReached
            ResultLimitReachedError -> ResultLimitReached
            in ServerSideError -> ServerSide
            else -> Generic
        }
    }

    private fun handleException(exception: IOException): State<Error> {
        return when (exception) {
            is ConnectException -> Connect
            is SocketTimeoutException -> SocketTimeout
            is SSLHandshakeException -> SSLHandshake
            is UnknownHostException -> UnknownHost
            else -> Generic
        }
    }

    private fun obtainTotalPages(headers: okhttp3.Headers): Int {
        var totalPages = 0
        headers[headerName]?.let { header ->
            if (header.isNotEmpty()) {
                totalPages = Regex(headerPattern).findAll(header)
                    .map(MatchResult::value)
                    .toList()[headerListIndex].toInt()
            }
        }
        return totalPages
    }

    private companion object {
        const val headerName = "link"
        val headerPattern = "\\d+".toPattern().toString()
        const val headerListIndex = 2
        val ClientSideError = 400..404
        const val SearchQuotaReachedError = 422
        const val ResultLimitReachedError = 451
        val ServerSideError = 500..511
    }
}
