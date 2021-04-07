package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

interface GenericProfileRepository {

    suspend fun provideGithubUserInformation(
        user: String = "",
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0
    ): Any
}
