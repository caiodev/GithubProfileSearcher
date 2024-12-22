package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel

fun interface IFetchLocalProfileUseCase {
    suspend operator fun invoke(
        shouldListBeCleared: Boolean,
        userProfileModel: UserProfileModel,
    ): List<UserProfile>
}
