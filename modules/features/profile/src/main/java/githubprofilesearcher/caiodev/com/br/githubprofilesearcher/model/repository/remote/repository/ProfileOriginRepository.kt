package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.calls.IProfileClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileOriginRepository(
    private val remoteFetcher: RemoteFetcher,
    private val client: IProfileClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IProfileOriginRepository {
    override suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> =
        withContext(dispatcher) {
            remoteFetcher.call<Profile> {
                client.fetchUserInfo(
                    user = user,
                    pageNumber = pageNumber,
                    maxResultsPerPage = maxResultsPerPage,
                )
            }
        }
}
