package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.callInterface.UserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository

class GithubRepositoryInformationRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: UserRepository
) : IGithubRepositoryInformationRepository {

    override suspend fun provideGithubUserInformation(user: String) =
        remoteRepository.callApi(call = {
            retrofitService.provideGithubUserInformationAsync(user)
        })

    override suspend fun provideGithubUserRepositoriesInformation(user: String) =
        remoteRepository.callApi(call = {
            retrofitService.provideGithubUserRepositoriesInformationAsync(user)
        })
}
