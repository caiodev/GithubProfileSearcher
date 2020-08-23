package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.callInterface

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.GithubProfileRepositoryInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRepository {

    @GET("users/{user}")
    suspend fun provideGithubUserInformationAsync(@Path("user") user: String): Response<GithubProfileInformation>

    @GET("users/{user}/repos")
    suspend fun provideGithubUserRepositoriesInformationAsync(@Path("user") user: String):
            Response<GithubProfileRepositoryInformation>
}
