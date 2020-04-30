package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.repository

import kotlinx.serialization.UnstableDefault

interface IGithubRepositoryInformationRepository {

    @UnstableDefault
    suspend fun provideGithubUserInformation(user: String = ""): Any

    @UnstableDefault
    suspend fun provideGithubUserRepositoriesInformation(user: String = ""): Any
}