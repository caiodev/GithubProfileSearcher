package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fiveHundred
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fiveHundredAndNinetyEight
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fourHundred
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fourHundredAndFour
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fourHundredAndNinetyNine
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fourHundredAndThree
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.fourHundredAndTwo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.genericError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.genericException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.successWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepository {

    suspend fun <T> callApi(
        call: suspend () -> Response<T>
    ): Any {
        return try {
            val response = call.invoke()

            if (response.isSuccessful) {
                handleSuccess(response)
            } else {
                handleError(response.code())
            }
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    private fun handleSuccess(response: Response<*>): Any {
        response.body()?.let { apiResponse ->
            return APICallResult.Success(apiResponse)
        } ?: run {
            return APICallResult.Success(successWithoutBody) // a.k.a 204 - No content
        }
    }

    private fun handleError(responseCode: Int): Any {
        return when (responseCode) {
            in fourHundred..fourHundredAndTwo,
            in fourHundredAndFour..fourHundredAndNinetyNine -> APICallResult.Error(clientSideError)
            fourHundredAndThree -> APICallResult.Error(forbidden)
            in fiveHundred..fiveHundredAndNinetyEight -> APICallResult.Error(serverSideError)
            else -> APICallResult.Error(genericError)
        }
    }

    private fun handleException(exception: Exception): Any {
        return when (exception) {
            is ConnectException -> APICallResult.Error(connectException)
            is SocketTimeoutException -> APICallResult.Error(socketTimeoutException)
            is SSLHandshakeException -> APICallResult.Error(sslHandshakeException)
            is UnknownHostException -> APICallResult.Error(unknownHostException)
            else -> APICallResult.Error(genericException)
        }
    }
}
