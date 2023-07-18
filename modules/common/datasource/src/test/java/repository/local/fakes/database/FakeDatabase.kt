package repository.local.fakes.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database

class FakeDatabase : Database, ProfileDao {

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
