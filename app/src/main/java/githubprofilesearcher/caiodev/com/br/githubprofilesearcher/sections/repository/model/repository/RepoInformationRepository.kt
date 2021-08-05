package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.callInterface.UserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository

class RepoInformationRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: UserRepository
) : IRepoInformationRepository {

    override suspend fun fetchUser(user: String) =
        remoteRepository.callApi(
            call = {
                retrofitService.fetchUser(user)
            }
        )

    override suspend fun fetchRepository(user: String) =
        remoteRepository.callApi(
            call = {
                retrofitService.fetchRepository(user)
            }
        )
}
