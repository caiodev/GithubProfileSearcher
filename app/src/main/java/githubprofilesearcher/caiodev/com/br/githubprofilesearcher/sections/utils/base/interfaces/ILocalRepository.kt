package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager

interface ILocalRepository : GithubProfilesDao {

    fun obtainProtoDataStore(): IKeyValueStorageManager

    /* GithubProfilesDao */
    override suspend fun getProfilesFromDb(): List<UserProfileInformation>
    override suspend fun insertProfilesIntoDb(githubProfilesList: List<UserProfileInformation>)
    override suspend fun dropProfileInformationTable(githubProfilesList: List<UserProfileInformation>)
}
