package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State

fun interface IProfileOriginRepository {
    suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*>
}
