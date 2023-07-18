package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager.IKeyValueStorageManager

class LocalFetcher(
    private val keyValueStorageManager: IKeyValueStorageManager,
    private val appDatabase: Database,
) : ILocalFetcher {

    override fun obtainProtoDataStore() = keyValueStorageManager

    override suspend fun getProfilesFromDb(): List<UserProfile> {
        var list = listOf<UserProfile>()
        appDatabase.profileDao().getProfilesFromDb()?.let {
            list = it
        }
        return list
    }

    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>) {
        appDatabase.profileDao()
            .insertProfilesIntoDb(profileList)
    }

    override suspend fun dropProfileInformation() {
        appDatabase.profileDao().dropProfileInformation()
    }
}