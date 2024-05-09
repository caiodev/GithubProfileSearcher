package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls

import io.ktor.client.statement.HttpResponse

fun interface IProfileClient {
    suspend fun fetchUserInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): HttpResponse
}
