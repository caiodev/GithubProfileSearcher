package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager

@Suppress("UNCHECKED_CAST")
class LocalRepository(
    private val keyValueStorageManager: IKeyValueStorageManager,
    private val appDatabase: Database
) : ILocalRepository {

    override fun obtainProtoDataStore() = keyValueStorageManager

    override suspend fun getProfilesFromDb(): List<UserProfileInformation> {
        var list = listOf<UserProfileInformation>()
        appDatabase.githubProfilesDao().getProfilesFromDb()?.let {
            list = it
        }
        return list
    }

    override suspend fun insertProfilesIntoDb(githubProfilesList: List<UserProfileInformation>) {
        appDatabase.githubProfilesDao()
            .insertProfilesIntoDb(githubProfilesList)
    }

    override suspend fun dropProfileInformationTable(githubProfilesList: List<UserProfileInformation>) {
        appDatabase.githubProfilesDao().dropProfileInformationTable(githubProfilesList)
    }
}
