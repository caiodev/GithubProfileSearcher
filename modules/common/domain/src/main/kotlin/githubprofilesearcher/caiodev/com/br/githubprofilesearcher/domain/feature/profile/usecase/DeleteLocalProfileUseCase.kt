package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository

class DeleteLocalProfileUseCase(
    private val profileDatabaseRepository: IProfileDatabaseRepository,
) : IDeleteLocalProfileUseCase {
    override suspend fun invoke() {
        profileDatabaseRepository.drop()
    }
}
