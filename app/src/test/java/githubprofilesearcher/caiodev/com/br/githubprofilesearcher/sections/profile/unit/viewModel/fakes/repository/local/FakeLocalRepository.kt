package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.viewModel.fakes.repository.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

class FakeLocalRepository : GenericLocalRepository {

    override fun obtainProtoDataStore() = FakeKeyValueStorageManager()

    override suspend fun getGithubProfilesFromDb(): List<UserProfile> {
        TODO("Not yet implemented")
    }

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<UserProfile>) {
        TODO("Not yet implemented")
    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<UserProfile>) {
        TODO("Not yet implemented")
    }
}
