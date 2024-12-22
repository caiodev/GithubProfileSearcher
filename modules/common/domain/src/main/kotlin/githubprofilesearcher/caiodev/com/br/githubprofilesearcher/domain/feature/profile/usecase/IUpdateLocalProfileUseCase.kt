package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile

fun interface IUpdateLocalProfileUseCase {
    suspend operator fun invoke(profileList: List<UserProfile>)
}
