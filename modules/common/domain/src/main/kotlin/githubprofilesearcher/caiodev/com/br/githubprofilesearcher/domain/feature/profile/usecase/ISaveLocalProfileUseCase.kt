package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile

fun interface ISaveLocalProfileUseCase {
    suspend operator fun invoke(profileList: List<UserProfile>)
}
