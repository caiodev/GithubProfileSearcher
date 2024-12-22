package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ProfileClient(
    private val client: HttpClient,
) : IProfileClient {
    override suspend fun fetchProfile(
        profile: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ) = client.get("search/users") {
        parameter("q", profile)
        parameter("page", pageNumber)
        parameter("per_page", maxResultsPerPage)
    }
}
