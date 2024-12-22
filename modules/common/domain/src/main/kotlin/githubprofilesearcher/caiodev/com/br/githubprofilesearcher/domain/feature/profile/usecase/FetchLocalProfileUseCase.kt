package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel

class FetchLocalProfileUseCase(
    private val deleteLocalProfileUseCase: IDeleteLocalProfileUseCase,
    private val obtainLocalProfileUseCase: IObtainLocalProfileUseCase,
    private val saveLocalProfileUseCase: ISaveLocalProfileUseCase,
    private val updateLocalProfileUseCase: IUpdateLocalProfileUseCase,
) : IFetchLocalProfileUseCase {
    override suspend fun invoke(
        shouldListBeCleared: Boolean,
        userProfileModel: UserProfileModel,
    ): List<UserProfile> {
        if (shouldListBeCleared) {
            deleteLocalProfileUseCase()
            saveLocalProfileUseCase(
                profileList = userProfileModel.profileList,
            )
        } else {
            updateLocalProfileUseCase(
                profileList = userProfileModel.profileList,
            )
        }
        return obtainLocalProfileUseCase()
    }
}
