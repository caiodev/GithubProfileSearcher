package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.calls.IProfileClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class ProfileOriginRepository(
    private val remoteFetcher: RemoteFetcher,
    private val client: IProfileClient,
    private val dispatcher: CoroutineDispatcher,
) : IProfileOriginRepository {
    override suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
    ): State<*> =
        withContext(dispatcher) {
            remoteFetcher.call<UserModel> {
                client.fetchUserInfo(
                    user = user,
                    pageNumber = pageNumber,
                    maxResultsPerPage = ITEMS_PER_PAGE,
                )
            }
        }
    companion object {
        const val ITEMS_PER_PAGE = 20
    }
}
