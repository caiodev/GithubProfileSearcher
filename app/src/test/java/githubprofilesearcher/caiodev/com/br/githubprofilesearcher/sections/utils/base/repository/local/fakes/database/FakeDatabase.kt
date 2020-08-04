package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.fakes.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database

class FakeDatabase : Database, GithubProfilesDao {

    override fun githubProfilesDao(): GithubProfilesDao {
        return this
    }

    override suspend fun getGithubProfilesFromDb() = listOf<GithubProfileInformation>()

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>) {

    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>) {

    }
}