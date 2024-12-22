package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.client

import io.ktor.client.statement.HttpResponse

fun interface IProfileClient {
    suspend fun fetchProfile(
        profile: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): HttpResponse
}
