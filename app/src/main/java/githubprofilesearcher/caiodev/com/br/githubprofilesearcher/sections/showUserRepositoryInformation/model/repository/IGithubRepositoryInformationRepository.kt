package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.repository

interface IGithubRepositoryInformationRepository {
    suspend fun provideGithubUserInformation(user: String = ""): Any
    suspend fun provideGithubUserRepositoriesInformation(user: String = ""): Any
}
