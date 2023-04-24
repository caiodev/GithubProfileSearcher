package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states

data class SuccessWithBody<T>(
    val data: T,
    val totalPages: Int = initialPosition
) : State<Success> {

    companion object {
        internal const val initialPosition = -1
    }
}
