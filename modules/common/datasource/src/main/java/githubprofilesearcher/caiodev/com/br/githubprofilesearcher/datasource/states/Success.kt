package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states

data class Success<T>(
    val data: T?,
    val totalPages: Int = INITIAL_POSITION,
) : SuccessState<T> {
    companion object {
        internal const val INITIAL_POSITION = -1
    }
}
