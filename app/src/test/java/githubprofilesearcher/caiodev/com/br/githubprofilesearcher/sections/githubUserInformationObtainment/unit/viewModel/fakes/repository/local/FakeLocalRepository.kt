package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

class FakeLocalRepository : GenericLocalRepository {

    override fun obtainProtoDataStore() = FakeKeyValueStorageManager()

    override suspend fun getGithubProfilesFromDb(): List<UserProfileInformation> {
        TODO("Not yet implemented")
    }

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<UserProfileInformation>) {
        TODO("Not yet implemented")
    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<UserProfileInformation>) {
        TODO("Not yet implemented")
    }
}
