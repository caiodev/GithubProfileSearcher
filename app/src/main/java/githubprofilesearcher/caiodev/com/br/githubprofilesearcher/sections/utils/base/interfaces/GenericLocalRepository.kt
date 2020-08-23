package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao

interface GenericLocalRepository :
    GithubProfilesDao {

    fun <T> retrieveValueFromSharedPreferences(
        key: String,
        defaultValue: T
    ): T

    fun <T> saveValueToSharedPreferences(
        key: String,
        value: T
    )

    fun clearSharedPreferences()

    /* GithubProfilesDao */
    override suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation>

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>)

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>)
}
