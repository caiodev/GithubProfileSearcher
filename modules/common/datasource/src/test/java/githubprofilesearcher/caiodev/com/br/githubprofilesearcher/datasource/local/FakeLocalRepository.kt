package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.local

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import repository.local.fakes.protoDataStore.manager.FakeKeyValueStorageManager

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
