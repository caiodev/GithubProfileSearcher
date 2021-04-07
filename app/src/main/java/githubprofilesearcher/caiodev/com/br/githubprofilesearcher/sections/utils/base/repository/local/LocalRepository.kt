package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager

@Suppress("UNCHECKED_CAST")
class LocalRepository(
    private val keyValueStorageManager: IKeyValueStorageManager,
    private val appDatabase: Database
) : GenericLocalRepository {

    override fun obtainProtoDataStore() = keyValueStorageManager

    override suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation> {
        var list = listOf<GithubProfileInformation>()
        appDatabase.githubProfilesDao().getGithubProfilesFromDb()?.let {
            list = it
        }
        return list
    }

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>) {
        appDatabase.githubProfilesDao()
            .insertGithubProfilesIntoDb(githubProfilesList)
    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>) {
        appDatabase.githubProfilesDao().dropGithubProfileInformationTable(githubProfilesList)
    }
}
