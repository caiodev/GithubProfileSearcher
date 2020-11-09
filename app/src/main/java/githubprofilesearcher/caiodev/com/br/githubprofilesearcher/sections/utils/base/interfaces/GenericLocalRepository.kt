package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager.IProtoDataStoreManager

interface GenericLocalRepository : GithubProfilesDao {

    fun obtainProtoDataStore(): IProtoDataStoreManager

    /* GithubProfilesDao */
    override suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation>

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>)

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>)
}
