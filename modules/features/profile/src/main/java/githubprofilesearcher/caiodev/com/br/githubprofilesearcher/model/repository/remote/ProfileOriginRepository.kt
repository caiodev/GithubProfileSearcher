package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileOriginRepository(
    private val remoteFetcher: RemoteFetcher,
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IProfileOriginRepository {

    override suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> =
        withContext(dispatcher) {
            remoteFetcher.call<Profile> {
                fetchUserInfo(
                    user = user,
                    pageNumber = pageNumber,
                    maxResultsPerPage = maxResultsPerPage
                )
            }
        }

    private suspend fun fetchUserInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ) = client.get("search/users") {
        parameter("q", user)
        parameter("page", pageNumber)
        parameter("per_page", maxResultsPerPage)
    }
}
