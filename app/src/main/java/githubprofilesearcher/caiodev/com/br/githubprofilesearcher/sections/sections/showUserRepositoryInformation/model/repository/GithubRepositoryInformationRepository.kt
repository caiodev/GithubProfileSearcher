package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showUserRepositoryInformation.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showUserRepositoryInformation.model.callInterface.UserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import kotlinx.serialization.UnstableDefault

class GithubRepositoryInformationRepository(private val retrofitService: UserRepository) :
    RemoteRepository(), IGithubRepositoryInformationRepository {

    @UnstableDefault
    override suspend fun provideGithubUserInformation(user: String) = callApi(call = {
        retrofitService.provideGithubUserInformationAsync(user)
    })

    @UnstableDefault
    override suspend fun provideGithubUserRepositoriesInformation(user: String) = callApi(call = {
        retrofitService.provideGithubUserRepositoriesInformationAsync(user)
    })
}