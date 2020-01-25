package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfilesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserProfile {

    /** https://github.com/square/okhttp/issues/1496  ||  https://github.com/square/okhttp/issues/3251 */

    @GET("search/users")
    suspend fun provideGithubUsersListAsync(
        @Query("q") user: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int
    ): Response<GithubProfilesList>
}