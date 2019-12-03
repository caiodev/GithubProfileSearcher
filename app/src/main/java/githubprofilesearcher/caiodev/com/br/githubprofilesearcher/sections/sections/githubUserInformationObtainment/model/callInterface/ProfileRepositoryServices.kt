package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfileRepositoryInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import retrofit2.Response
import retrofit2.http.*

interface ProfileRepositoryServices {

    /** https://github.com/square/okhttp/issues/1496  ||  https://github.com/square/okhttp/issues/3251 */

    @GET("search/users")
    suspend fun provideGithubUsersListAsync(
        @Query("q") user: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int
    ): Response<GithubProfilesList>

    @GET("users/{user}")
    suspend fun provideGithubUserInformationAsync(@Path("user") user: String): Response<GithubProfileInformation>

    @GET("users/{user}/repos")
    suspend fun provideGithubUserRepositoriesInformationAsync(@Path("user") user: String):
            Response<GithubProfileRepositoryInformation>
}