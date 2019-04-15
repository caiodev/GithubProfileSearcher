package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base

import android.system.ErrnoException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class RemoteRepository {

    private var errorResponse = Any()

    protected suspend fun <T : Any> callApi(
        call: suspend () -> Response<T>
    ): Any {

        try {

            val response = call.invoke()

            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    return APICallResult.Success(apiResponse)
                }

            } else {
                errorResponse = response.message()
            }

        } catch (exception: Exception) {
            when (exception) {
                is UnknownHostException, is SocketTimeoutException, is ConnectException, is ErrnoException -> return APICallResult.ConnectionError
            }
        }

        return APICallResult.InternalError(errorResponse)
    }
}