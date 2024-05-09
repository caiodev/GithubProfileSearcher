package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.handler

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ErrorState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.UnknownHost
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

inline fun <reified S, T> State<*>.handleResult(
    onSuccess: (success: S?) -> T,
    crossinline onFailure: (error: Error) -> T,
): T =
    if (this is Success<*>) {
        onSuccess(ValueCasting.castTo<S>(this.data))
    } else {
        onFailure(handleError(ValueCasting.castTo(this)))
    }

@PublishedApi
internal fun handleError(errorState: ErrorState?): Error {
    when (errorState) {
        ClientSide -> Core.string.client_side
        Connect, UnknownHost, SocketTimeout -> Core.string.unknown_host_and_socket_timeout
        ResultLimitReached -> Core.string.limit_of_profile_results
        SearchQuotaReached -> Core.string.query_limit
        ServerSide -> Core.string.server_side
        SSLHandshake -> Core.string.ssl_handshake
        else -> Core.string.generic
    }.apply { return Error(error = this) }
}
