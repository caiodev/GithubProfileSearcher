package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import kotlinx.serialization.UnstableDefault

class GithubProfileInformationRepository(private val retrofitService: UserProfile) :
    RemoteRepository(),
    Repository {

    @UnstableDefault
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = callApi(call = {
        retrofitService.provideGithubUsersListAsync(
            user,
            pageNumber,
            maxResultsPerPage
        )
    })
}