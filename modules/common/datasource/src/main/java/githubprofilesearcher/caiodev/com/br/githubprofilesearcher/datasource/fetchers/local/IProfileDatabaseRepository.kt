package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile

interface IProfileDatabaseRepository : ProfileDao {

    override suspend fun getProfilesFromDb(): List<UserProfile>
    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)
    override suspend fun dropProfileInformation()
}
