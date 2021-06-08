package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states

data class Success<T>(val data: T, val totalPages: Int = 0) : State<T>
