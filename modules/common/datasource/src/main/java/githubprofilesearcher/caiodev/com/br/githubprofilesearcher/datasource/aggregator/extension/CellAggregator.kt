package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.extension

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.ICell
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

suspend fun ICell.handleResult(
    value: State<*>,
    onSuccess: suspend (success: Success<*>) -> Unit = {},
    onFailure: suspend (error: Error) -> Unit = {},
) {
    when (value) {
        is Success<*> -> onSuccess(value)
        else -> onFailure(handleError(ValueCasting.castTo(value)))
    }
}

private fun handleError(errorState: ErrorState?): Error {
    val error: Int =
        when (errorState) {
            UnknownHost, SocketTimeout, Connect -> Core.string.unknown_host_and_socket_timeout
            SSLHandshake -> Core.string.ssl_handshake
            ClientSide -> Core.string.client_side
            ServerSide -> Core.string.server_side
            SearchQuotaReached -> Core.string.query_limit
            ResultLimitReached -> Core.string.limit_of_profile_results
            else -> Core.string.generic
        }
    return Error(error = error)
}
