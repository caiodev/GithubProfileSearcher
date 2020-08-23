package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service

sealed class APICallResult<out T> {
    data class Success<out T>(val data: T) : APICallResult<T>()
    data class Error(val error: Int) : APICallResult<Int>()
}
