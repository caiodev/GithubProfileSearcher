package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository

class ObtainLocalProfileUseCase(
    private val profileDatabaseRepository: IProfileDatabaseRepository,
) : IObtainLocalProfileUseCase {
    override suspend fun invoke(): List<UserProfile> = profileDatabaseRepository.get()
}
