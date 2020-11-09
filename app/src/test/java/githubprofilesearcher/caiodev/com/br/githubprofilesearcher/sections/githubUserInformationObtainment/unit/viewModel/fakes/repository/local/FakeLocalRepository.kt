package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

class FakeLocalRepository : GenericLocalRepository {

    override fun obtainProtoDataStore() = FakeProtoDataStoreManager()

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
