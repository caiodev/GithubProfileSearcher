package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface.ProfileRepositoryServices
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit
import kotlinx.serialization.UnstableDefault

class GithubProfileInformationRepository : RemoteRepository() {

    @UnstableDefault
    private val retrofitService = Retrofit().provideRetrofitService<ProfileRepositoryServices>()

    @UnstableDefault
    suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ): Any {
        return callApi(call = {
            retrofitService.provideGithubUsersListAsync(user, pageNumber, maxResultsPerPage)
        })
    }

    @UnstableDefault
    suspend fun provideGithubUserInformation(user: String): Any {
        return callApi(call = {
            retrofitService.provideGithubUserInformationAsync(user)
        })
    }

    @UnstableDefault
    suspend fun provideGithubUserRepositoriesInformation(user: String): Any {
        return callApi(call = {
            retrofitService.provideGithubUserRepositoriesInformationAsync(user)
        })
    }
}