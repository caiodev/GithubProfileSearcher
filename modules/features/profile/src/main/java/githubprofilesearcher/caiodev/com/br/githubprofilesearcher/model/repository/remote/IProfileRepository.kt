package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

interface IProfileRepository {

    suspend fun provideUserInformation(
        user: String = "",
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0,
    ): Any
}
