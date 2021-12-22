package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository

class ProfileRepository(
    private val remoteRepository: RemoteRepository,
    private val apiService: UserProfile
) : IProfileRepository {

    override suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = remoteRepository.call {
        apiService.provideUsers(
            user,
            pageNumber,
            maxResultsPerPage
        )
    }
}
