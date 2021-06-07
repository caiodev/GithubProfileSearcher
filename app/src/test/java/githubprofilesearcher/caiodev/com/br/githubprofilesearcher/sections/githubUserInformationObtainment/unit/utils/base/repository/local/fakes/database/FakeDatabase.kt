package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database

class FakeDatabase : Database, GithubProfilesDao {

    override fun githubProfilesDao(): GithubProfilesDao {
        return this
    }

    override suspend fun getProfilesFromDb() = listOf<UserProfileInformation>()

    override suspend fun insertProfilesIntoDb(githubProfilesList: List<UserProfileInformation>) {
        //
    }

    override suspend fun dropProfileInformationTable(githubProfilesList: List<UserProfileInformation>) {
        //
    }
}
