package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.Profile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserProfile {
    @GET("search/users")
    suspend fun provideUsers(
        @Query("q") user: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int,
    ): Response<Profile>
}
