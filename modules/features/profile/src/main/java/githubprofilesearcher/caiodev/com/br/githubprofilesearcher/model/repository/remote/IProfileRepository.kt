package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

fun interface IProfileRepository {

    suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): Any
}
