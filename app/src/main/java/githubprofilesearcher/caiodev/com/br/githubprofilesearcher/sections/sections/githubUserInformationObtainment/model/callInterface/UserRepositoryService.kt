package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubUserRepositoryInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubUsersList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubUserInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserRepositoryService {

    @GET("search/users")
    suspend fun getGithubUsersListAsync(
        @Query("q") user: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int
    ): Response<GithubUsersList>

    @GET("users/{user}")
    suspend fun getGithubUserInformationAsync(@Path("user") user: String): Response<GithubUserInformation>

    @GET("users/{user}/repos")
    suspend fun getGithubUserRepositoriesInformationAsync(@Path("user") user: String):
            Response<GithubUserRepositoryInformation>
}