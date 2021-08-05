package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository

class ProfileRepository(
    private val remoteRepository: RemoteRepository,
    private val apiService: UserProfile
) : IProfileRepository {

    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = remoteRepository.callApi(
        call = {
            apiService.provideUsers(
                user,
                pageNumber,
                maxResultsPerPage
            )
        }
    )
}
