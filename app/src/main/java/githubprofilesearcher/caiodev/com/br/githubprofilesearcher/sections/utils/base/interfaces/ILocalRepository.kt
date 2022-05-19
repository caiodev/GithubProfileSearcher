package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager

interface ILocalRepository : ProfileDao {

    fun obtainProtoDataStore(): IKeyValueStorageManager

    override suspend fun getProfilesFromDb(): List<UserProfile>
    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)
    override suspend fun dropProfileInformation()
}
