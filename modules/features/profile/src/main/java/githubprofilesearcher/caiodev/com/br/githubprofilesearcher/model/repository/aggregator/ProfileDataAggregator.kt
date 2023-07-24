package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository

class ProfileDataAggregator(
    private val keyValueRepository: IKeyValueRepository,
    private val profileDatabaseRepository: IProfileDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataAggregator {

    override suspend fun <T> getValue(key: Enum<*>): T {
        return keyValueRepository.getValue(key = key)
    }

    override suspend fun <T> setValue(key: Enum<*>, value: T) {
        keyValueRepository.setValue(key = key, value = value)
    }

    override suspend fun getProfilesFromDb(): List<UserProfile> {
        return profileDatabaseRepository.getProfilesFromDb()
    }

    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>) {
        profileDatabaseRepository.insertProfilesIntoDb(profileList = profileList)
    }

    override suspend fun dropProfileInformation() {
        profileDatabaseRepository.dropProfileInformation()
    }

    override suspend fun provideUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> {
        return profileOriginRepository.provideUserInformation(
            user = user,
            pageNumber = pageNumber,
            maxResultsPerPage = maxResultsPerPage,
        )
    }
}
