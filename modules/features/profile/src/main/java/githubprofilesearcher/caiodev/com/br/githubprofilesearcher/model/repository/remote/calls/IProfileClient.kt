package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.calls

import io.ktor.client.statement.HttpResponse

fun interface IProfileClient {
    suspend fun fetchUserInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): HttpResponse
}
