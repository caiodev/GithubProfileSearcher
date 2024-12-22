package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository

class SaveLocalProfileUseCase(
    private val profileDatabaseRepository: IProfileDatabaseRepository,
) : ISaveLocalProfileUseCase {
    override suspend fun invoke(profileList: List<UserProfile>) {
        profileDatabaseRepository.insert(profileList)
    }
}
