package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states

private const val INITIAL_POSITION = -1

data class Success<T>(
    val data: T?,
    val totalPages: Int = INITIAL_POSITION,
) : SuccessState<T>
