package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.extension

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ClientSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Connect
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ErrorWithMessage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ResultLimitReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SSLHandshake
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SearchQuotaReached
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ServerSide
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SocketTimeout
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.UnknownHost
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.ICell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

suspend fun ICell.handleResult(
    value: State<*>,
    onSuccess: suspend () -> Unit = {},
): State<*> =
    when (value) {
        is SuccessWithBody<*> -> {
            onSuccess()
            value
        }
        is SuccessWithoutBody -> value
        else -> handleError(ValueCasting.castTo(value))
    }

fun ICell.handleError(error: State<Error>?): ErrorWithMessage {
    val errorMessage: Int =
        when (error) {
            UnknownHost, SocketTimeout, Connect -> Core.string.unknown_host_and_socket_timeout
            SSLHandshake -> Core.string.ssl_handshake
            ClientSide -> Core.string.client_side
            ServerSide -> Core.string.server_side
            SearchQuotaReached -> Core.string.query_limit
            ResultLimitReached -> Core.string.limit_of_profile_results
            else -> Core.string.generic
        }

    return ErrorWithMessage(errorMessage)
}
