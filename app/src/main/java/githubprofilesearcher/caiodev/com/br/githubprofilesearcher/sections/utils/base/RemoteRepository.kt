package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.genericError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.genericException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.successWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

open class RemoteRepository {

    protected suspend fun <T : Any> callApi(
        call: suspend () -> Response<T>
    ): Any {

        try {

            val response = call.invoke()

            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    return APICallResult.Success(apiResponse)
                } ?: run {
                    return APICallResult.Success(APICallResult.Success(successWithoutBody)) //a.k.a 204
                }
            } else {
                when (response.code()) {
                    in 400..450 -> APICallResult.Error(clientSideError)
                    in 500..598 -> APICallResult.Error(serverSideError)
                }
            }

        } catch (exception: Exception) {
            return when (exception) {
                is UnknownHostException -> APICallResult.Error(
                    unknownHostException
                )
                is SocketTimeoutException -> APICallResult.Error(socketTimeoutException)
                is SSLHandshakeException -> APICallResult.Error(sslHandshakeException)
                else -> APICallResult.Error(genericException)
            }
        }

        return APICallResult.Error(genericError)
    }
}