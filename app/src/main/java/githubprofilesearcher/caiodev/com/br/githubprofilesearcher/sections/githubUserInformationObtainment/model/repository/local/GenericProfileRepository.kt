package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local

interface GenericProfileRepository {

    suspend fun provideGithubUserInformation(
        user: String = "",
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0
    ): Any
}
