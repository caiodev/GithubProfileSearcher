package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service

sealed class APICallResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : APICallResult<T>()
    data class InternalError<out T : Any>(val error: T) : APICallResult<T>()
    object ConnectionError : APICallResult<Nothing>()
}