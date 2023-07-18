package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfileCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(
    private val remoteRepository: RemoteFetcher,
    private val apiService: UserProfileCall,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IProfileRepository {

    override suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> =
        withContext(dispatcher) {
            remoteRepository.call {
                apiService.provideUsers(
                    user,
                    pageNumber,
                    maxResultsPerPage,
                )
            }
        }
}
