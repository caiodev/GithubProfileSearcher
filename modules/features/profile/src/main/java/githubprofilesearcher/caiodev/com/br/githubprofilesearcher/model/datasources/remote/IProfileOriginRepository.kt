package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.datasources.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.State

fun interface IProfileOriginRepository {
    suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
    ): State<*>
}
