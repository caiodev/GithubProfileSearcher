package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states

data class Success<T>(
    val data: T?,
    val totalPages: Int = INITIAL_POSITION,
) : State<Success<T>> {
    companion object {
        internal const val INITIAL_POSITION = -1
    }
}
