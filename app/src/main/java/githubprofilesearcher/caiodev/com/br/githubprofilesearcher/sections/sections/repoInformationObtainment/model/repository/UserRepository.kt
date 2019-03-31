package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.callInterface.UserRepositoryService
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.Repository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.RetrofitService

class UserRepository : Repository() {

    private var retrofitService = RetrofitService()

    suspend fun getRepositoryInformation(user: String): Any {

        retrofitService.getRetrofitService<UserRepositoryService>().loginAsync(user).await()

        return callApi(call = {
            retrofitService.getRetrofitService<UserRepositoryService>().loginAsync(user).await()
        })
    }
}