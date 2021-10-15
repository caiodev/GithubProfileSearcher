package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.*
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepository {

    suspend fun <T> callApi(
        call: suspend () -> Response<T>
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
                return SuccessWithoutBody
            }
        }
    }

    private fun handleHttpError(responseCode: Int): State<Error> {
        return when (responseCode) {
            in `400`..`402`, in `404`..`451` -> ClientSide
            `403` -> SearchQuotaReached
            `422` -> SearchLimitReached
            in `500`..`511` -> ServerSide
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
        headers[headerParameterName]?.let { header ->
            if (header.isNotEmpty()) {
                totalPages = Regex(headerParameterPattern).findAll(header)
                    .map(MatchResult::value)
                    .toList()[headerListIndex].toInt()
            }
        }
        return totalPages
    }

    companion object {
        private const val `400` = 400
        private const val `402` = 402
        private const val `403` = 403
        private const val `404` = 404
        private const val `422` = 422
        private const val `451` = 451
        private const val `500` = 500
        private const val `511` = 511

        private const val headerParameterName = "link"
        private val headerParameterPattern = "[0-9]+".toPattern().toString()
        private const val headerListIndex = 2
    }
}
