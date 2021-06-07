package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.States
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepository {

    private val fourHundred = 400
    private val fourHundredAndTwo = 402
    private val fourHundredAndThree = 403
    private val fourHundredAndFour = 404
    private val fourHundredAndTwentyTwo = 422
    private val fourHundredAndNinetyNine = 499
    private val fiveHundred = 500
    private val fiveHundredAndNinetyEight = 598

    private val headerParameterName = "link"
    private val headerParameterPattern = "[0-9]+".toPattern().toString()
    private val headerListIndex = 2

    suspend fun <T> callApi(
        call: suspend () -> Response<T>
    ): Any {
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

    private fun handleSuccess(response: Response<*>): Any {
        with(response) {
            body()?.let { apiResponse ->
                return States.Success(apiResponse, obtainTotalPages(headers()))
            } ?: run {
                return States.SuccessWithoutBody
            }
        }
    }

    private fun handleHttpError(responseCode: Int): Any {
        return when (responseCode) {
            in fourHundred..fourHundredAndTwo,
            in fourHundredAndFour..fourHundredAndNinetyNine -> States.ClientSide
            fourHundredAndThree -> States.Forbidden
            in fiveHundred..fiveHundredAndNinetyEight -> States.ServerSide
            else -> States.Generic
        }
    }

    private fun handleException(exception: IOException): Any {
        return when (exception) {
            is ConnectException -> States.Connect
            is SocketTimeoutException -> States.SocketTimeout
            is SSLHandshakeException -> States.SSLHandshake
            is UnknownHostException -> States.UnknownHost
            else -> States.Generic
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
}
