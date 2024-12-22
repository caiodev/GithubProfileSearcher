package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel
import kotlinx.coroutines.flow.Flow

fun interface IProfileRemoteRepository {
    suspend fun fetchProfileInfo(
        profile: String,
        pageNumber: Int,
    ): Pair<Flow<UserProfileModel?>, Flow<Int>>
}
