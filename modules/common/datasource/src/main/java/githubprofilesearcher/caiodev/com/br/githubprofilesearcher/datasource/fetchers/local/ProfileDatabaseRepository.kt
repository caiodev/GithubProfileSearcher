package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database

class ProfileDatabaseRepository(
    private val appDatabase: Database,
) : IProfileDatabaseRepository {

    override suspend fun getProfilesFromDb(): List<UserProfile> {
        var list = emptyList<UserProfile>()
        appDatabase.profileDao().getProfilesFromDb()?.let {
            list = it
        }
        return list
    }

    override suspend fun insertProfilesIntoDb(profileList: List<UserProfile>) {
        appDatabase.profileDao()
            .insertProfilesIntoDb(profileList)
    }

    override suspend fun dropProfileInformation() {
        appDatabase.profileDao().dropProfileInformation()
    }
}
