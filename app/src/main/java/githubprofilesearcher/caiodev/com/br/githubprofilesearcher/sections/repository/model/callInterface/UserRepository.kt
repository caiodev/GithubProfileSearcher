package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.RepositoryInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRepository {

    @GET("users/{user}")
    suspend fun fetchUser(@Path("user") user: String): Response<UserProfile>

    @GET("users/{user}/repos")
    suspend fun fetchRepository(@Path("user") user: String): Response<RepositoryInformation>
}
