package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager

@Suppress("UNCHECKED_CAST")
class LocalRepository(
    private val keyValueStorageManager: IKeyValueStorageManager,
    private val appDatabase: Database
) : ILocalRepository {

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
