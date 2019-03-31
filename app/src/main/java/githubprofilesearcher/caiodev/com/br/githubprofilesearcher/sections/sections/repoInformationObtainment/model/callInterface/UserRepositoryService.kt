package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.UserRepositoryInformation
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRepositoryService {

    @GET("users/{user}")
    fun loginAsync(@Path("user") user: String): Deferred<Response<UserRepositoryInformation>>
}