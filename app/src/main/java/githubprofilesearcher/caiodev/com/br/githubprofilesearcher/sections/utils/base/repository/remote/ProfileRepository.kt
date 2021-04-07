package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.callInterface.UserProfile

class ProfileRepository(
    private val remoteRepository: RemoteRepository,
    private val apiService: UserProfile
) : GenericProfileRepository {

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
