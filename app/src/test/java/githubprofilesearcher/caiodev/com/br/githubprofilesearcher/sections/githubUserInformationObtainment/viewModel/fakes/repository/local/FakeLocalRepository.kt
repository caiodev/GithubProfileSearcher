package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

class FakeLocalRepository : GenericLocalRepository {

    override fun <T> retrieveValueFromSharedPreferences(key: String, defaultValue: T): T {
        TODO("Not yet implemented")
    }

    override fun <T> saveValueToSharedPreferences(key: String, value: T) {
        TODO("Not yet implemented")
    }

    override fun clearSharedPreferences() {
        TODO("Not yet implemented")
    }

    override suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation> {
        TODO("Not yet implemented")
    }

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>) {
        TODO("Not yet implemented")
    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>) {
        TODO("Not yet implemented")
    }
}