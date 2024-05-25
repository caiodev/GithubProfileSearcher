package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.State
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ProfileOriginRepository(
    private val dispatcher: CoroutineContext,
    private val client: IProfileClient,
    private val remoteFetcher: RemoteFetcher,
) : IProfileOriginRepository {
    override suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
        isCoroutineActive: () -> Boolean,
    ): State<*> =
        withContext(dispatcher) {
            remoteFetcher.call<UserModel>(
                call = {
                    client.fetchUserInfo(
                        user = user,
                        pageNumber = pageNumber,
                        maxResultsPerPage = ITEMS_PER_PAGE,
                    )
                },
                isCoroutineActive = isCoroutineActive,
            )
        }

    companion object {
        const val ITEMS_PER_PAGE = 20
    }
}
