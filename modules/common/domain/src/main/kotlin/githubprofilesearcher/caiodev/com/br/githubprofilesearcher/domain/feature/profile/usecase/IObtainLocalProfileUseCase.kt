package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile

fun interface IObtainLocalProfileUseCase {
    suspend operator fun invoke(): List<UserProfile>
}
