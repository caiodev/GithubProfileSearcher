package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.callInterface.UserProfile
import kotlinx.serialization.UnstableDefault

class GithubProfileRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: UserProfile
) : GenericGithubProfileRepository {

    @UnstableDefault
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = remoteRepository.callApi(call = {
        retrofitService.provideGithubUsersListAsync(
            user,
            pageNumber,
            maxResultsPerPage
        )
    })
}