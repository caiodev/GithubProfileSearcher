package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

fun interface UserProfileCall {
    @GET("search/users")
    suspend fun provideUsers(
        @Query("q") user: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") maxQuantityPerPage: Int,
    ): Response<Profile>
}
