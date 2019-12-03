package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface.ProfileRepositoryServices
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.RetrofitService

class GithubProfileInformationRepository : RemoteRepository() {

    private val retrofitService = RetrofitService().provideRetrofitService<ProfileRepositoryServices>()

    suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ): Any {
        return callApi(call = {
            retrofitService.provideGithubUsersListAsync(user, pageNumber, maxResultsPerPage)
        })
    }

    suspend fun provideGithubUserInformation(user: String): Any {
        return callApi(call = {
            retrofitService.provideGithubUserInformationAsync(user)
        })
    }

    suspend fun provideGithubUserRepositoriesInformation(user: String): Any {
        return callApi(call = {
            retrofitService.provideGithubUserRepositoriesInformationAsync(user)
        })
    }
}