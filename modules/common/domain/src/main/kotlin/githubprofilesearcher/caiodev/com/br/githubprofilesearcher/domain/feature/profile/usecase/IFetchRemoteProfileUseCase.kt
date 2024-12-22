package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel
import kotlinx.coroutines.flow.Flow

fun interface IFetchRemoteProfileUseCase {
    suspend operator fun invoke(
        profile: String,
        shouldListBeCleared: Boolean,
    ): Pair<Flow<UserProfileModel?>, Flow<Int>>
}
