package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SearchLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SuccessWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.UnknownHost
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
        with(response) {
            body()?.let { apiResponse ->
                return SuccessWithBody(apiResponse, obtainTotalPages(headers()))
            } ?: run {
                return SuccessWithoutBody()
            }
        }
    }

    private fun handleHttpError(responseCode: Int): State<Error> {
        return when (responseCode) {
            in Error400..Error402, in Error403..Error404 -> ClientSide
            Error422 -> SearchQuotaReached
            Error451 -> SearchLimitReached
            in Error500..Error511 -> ServerSide
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

    companion object {
        private const val headerName = "link"
        private val headerPattern = "\\d+".toPattern().toString()
        private const val headerListIndex = 2
        private const val Error400 = 400
        private const val Error402 = 402
        private const val Error403 = 403
        private const val Error404 = 404
        private const val Error422 = 422
        private const val Error451 = 451
        private const val Error500 = 500
        private const val Error511 = 511
    }
}
