package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.model.GithubProfileRepositoryInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRepository {

    @GET("users/{user}")
    suspend fun provideGithubUserInformationAsync(@Path("user") user: String): Response<UserProfileInformation>

    @GET("users/{user}/repos")
    suspend fun provideGithubUserRepositoriesInformationAsync(@Path("user") user: String):
        Response<GithubProfileRepositoryInformation>
}
