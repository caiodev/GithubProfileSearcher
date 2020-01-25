package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import kotlinx.serialization.UnstableDefault

class GithubProfileInformationRepository(
    private val remoteRepository: RemoteRepository,
    private val retrofitService: UserProfile
) : Repository {

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