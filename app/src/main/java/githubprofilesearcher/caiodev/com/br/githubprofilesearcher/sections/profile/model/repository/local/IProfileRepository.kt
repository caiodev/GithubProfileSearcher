package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local

interface IProfileRepository {

    suspend fun provideGithubUserInformation(
        user: String = "",
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0
    ): Any
}