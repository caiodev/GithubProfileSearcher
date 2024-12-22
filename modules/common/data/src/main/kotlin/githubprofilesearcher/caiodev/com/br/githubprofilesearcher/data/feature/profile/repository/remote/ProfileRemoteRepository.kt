package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.remote.fetcher.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.mapper.mapFromRemote
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.client.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.model.ProfileModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.repository.remote.handler.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ProfileRemoteRepository(
    private val dispatcher: CoroutineContext,
    private val client: IProfileClient,
    private val remoteFetcher: RemoteFetcher,
) : IProfileRemoteRepository {
    override suspend fun fetchProfileInfo(
        profile: String,
        pageNumber: Int,
    ): Pair<Flow<UserProfileModel?>, Flow<Int>> =
        withContext(dispatcher) {
            remoteFetcher
                .call<ProfileModel>(
                    call = {
                        client.fetchProfile(
                            profile = profile,
                            pageNumber = pageNumber,
                            maxResultsPerPage = ITEMS_PER_PAGE,
                        )
                    },
                )
                .handleResult<ProfileModel, Pair<Flow<UserProfileModel?>, Flow<Int>>>(
                    onSuccess = { result ->
                        provideUserProfileResultFlows(success = flow { emit(result?.mapFromRemote()) })
                    },
                    onFailure = { result ->
                        provideUserProfileResultFlows(error = flow { emit(result) })
                    },
                )
        }

    private fun provideUserProfileResultFlows(
        success: Flow<UserProfileModel?> = emptyFlow<UserProfileModel?>(),
        error: Flow<Int> = emptyFlow<Int>(),
    ): Pair<Flow<UserProfileModel?>, Flow<Int>> = Pair(first = success, second = error)

    companion object {
        const val ITEMS_PER_PAGE = 20
    }
}
