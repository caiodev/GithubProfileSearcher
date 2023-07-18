package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager.IKeyValueStorageManager

interface ILocalFetcher : ProfileDao {

    fun obtainProtoDataStore(): IKeyValueStorageManager

    override suspend fun getProfilesFromDb(): List<UserProfile>
    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)
    override suspend fun dropProfileInformation()
}
