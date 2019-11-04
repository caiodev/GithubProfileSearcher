package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface.UserRepositoryService
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.RetrofitService

class GithubUserInformationRepository : RemoteRepository() {

    private val retrofitService = RetrofitService().getRetrofitService<UserRepositoryService>()

    suspend fun getGithubUserList(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ): Any {
        return callApi(call = {
            retrofitService.getGithubUsersListAsync(user, pageNumber, maxResultsPerPage)
        })
    }

    suspend fun getGithubUserInformation(user: String): Any {
        return callApi(call = {
            retrofitService.getGithubUserInformationAsync(user)
        })
    }

    suspend fun getGithubUserRepositoriesInformation(user: String): Any {
        return callApi(call = {
            retrofitService.getGithubUserRepositoriesInformationAsync(user)
        })
    }
}