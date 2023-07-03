package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.utils.base.repository.local.fakes.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.db.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile

class FakeDatabase : githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.db.Database,
    ProfileDao {

    override fun profileDao(): ProfileDao {
        return this
    }

    override suspend fun getProfilesFromDb() = listOf<UserProfile>()

    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>) {
        //
    }

    override suspend fun dropProfileInformation(githubProfilesList: List<UserProfile>) {
        //
    }
}
