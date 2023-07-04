package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfileCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.remote.RemoteRepository

class ProfileRepository(
    private val remoteRepository: githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.remote.RemoteRepository,
    private val apiService: UserProfileCall,
) : IProfileRepository {

    override suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ) = remoteRepository.call {
        apiService.provideUsers(
            user,
            pageNumber,
            maxResultsPerPage,
        )
    }
}
