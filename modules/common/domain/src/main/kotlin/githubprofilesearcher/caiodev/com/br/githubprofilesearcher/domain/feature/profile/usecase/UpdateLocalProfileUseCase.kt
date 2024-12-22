package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository

class UpdateLocalProfileUseCase(
    private val profileDatabaseRepository: IProfileDatabaseRepository,
) : IUpdateLocalProfileUseCase {
    override suspend fun invoke(profileList: List<UserProfile>) {
        profileDatabaseRepository.upsert(profileList = profileList)
    }
}
