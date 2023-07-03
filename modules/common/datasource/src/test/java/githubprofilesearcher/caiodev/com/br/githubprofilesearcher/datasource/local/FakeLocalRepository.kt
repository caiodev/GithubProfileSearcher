package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.models.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.utils.base.repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

class FakeLocalRepository : GenericLocalRepository {

    override fun obtainProtoDataStore() =
        githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.utils.base.repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager()

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
