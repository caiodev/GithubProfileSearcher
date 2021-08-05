package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local.fakes.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database

class FakeDatabase : Database, ProfileDao {

    override fun profileDao(): ProfileDao {
        return this
    }

    override suspend fun getProfilesFromDb() = listOf<UserProfile>()

    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>) {
        //
    }

    override suspend fun dropProfileInformationTable(githubProfilesList: List<UserProfile>) {
        //
    }
}
