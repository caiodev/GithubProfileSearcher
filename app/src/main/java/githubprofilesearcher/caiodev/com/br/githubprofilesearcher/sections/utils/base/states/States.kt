package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states

sealed class States<out T> {
    data class Success<out T>(val data: T, val totalPages: Int = 0) : States<T>()
    object SuccessWithoutBody : States<Nothing>()
    object Error
    object ClientSide : States<Error>()
    object Connect : States<Error>()
    object Forbidden : States<Error>()
    object Generic : States<Error>()
    object ServerSide : States<Error>()
    object SocketTimeout : States<Error>()
    object SSLHandshake : States<Error>()
    object UnknownHost : States<Error>()
}
