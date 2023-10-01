package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.calls

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ProfileClient(
    private val client: HttpClient,
) : IProfileClient {
    override suspend fun fetchUserInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ) = client.get("search/users") {
        parameter("q", user)
        parameter("page", pageNumber)
        parameter("per_page", maxResultsPerPage)
    }
}
