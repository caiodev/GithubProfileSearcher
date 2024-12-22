package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.state

private const val INITIAL_POSITION = -1

data class Success<T>(
    val data: T?,
    val totalPages: Int = INITIAL_POSITION,
) : SuccessState<T>
