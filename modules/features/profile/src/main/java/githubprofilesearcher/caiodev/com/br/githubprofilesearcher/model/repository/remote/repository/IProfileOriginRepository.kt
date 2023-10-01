package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State

fun interface IProfileOriginRepository {
    suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*>
}
