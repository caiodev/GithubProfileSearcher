package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileRemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import kotlinx.coroutines.flow.Flow

class FetchRemoteProfileUseCase(
    private val profileRepository: IProfileRemoteRepository,
) : IFetchRemoteProfileUseCase {
    private var profileName: String = emptyString()
    private var pageNumber = INITIAL_PAGE

    override suspend fun invoke(
        profile: String,
        shouldListBeCleared: Boolean,
    ): Pair<Flow<UserProfileModel?>, Flow<Int>> {
        if (profile.isNotEmpty()) profileName = profile
        pageNumber = if (shouldListBeCleared) INITIAL_PAGE else ++pageNumber
        return profileRepository.fetchProfileInfo(
            profile = profileName,
            pageNumber = pageNumber,
        )
    }

    private companion object {
        const val INITIAL_PAGE = 1
    }
}
