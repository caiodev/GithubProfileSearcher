package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.repository.remote.handler

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ErrorState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.MaximumResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state.UnknownHost
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources

suspend inline fun <reified S, T> State<*>.handleResult(
    crossinline onSuccess: suspend (success: S?) -> T,
    crossinline onFailure: (error: Int) -> T,
): T =
    if (this is Success<*>) {
        onSuccess(data.castTo<S>())
    } else {
        onFailure(handleError(castTo()))
    }

@PublishedApi
internal fun handleError(errorState: ErrorState?): Int =
    when (errorState) {
        ClientSide -> Resources.string.client_side
        Connect, SocketTimeout, UnknownHost -> Resources.string.unknown_host_and_socket_timeout
        MaximumResultLimitReached -> Resources.string.limit_of_profile_results
        SearchQuotaReached -> Resources.string.query_limit
        ServerSide -> Resources.string.server_side
        SSLHandshake -> Resources.string.ssl_handshake
        else -> Resources.string.generic
    }
