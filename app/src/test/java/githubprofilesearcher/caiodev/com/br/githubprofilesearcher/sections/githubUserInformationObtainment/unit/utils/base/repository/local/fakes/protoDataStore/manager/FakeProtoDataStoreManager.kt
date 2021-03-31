package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dataStore.model.ProfilePreferences

class FakeProtoDataStoreManager : IProtoDataStoreManager {

    private var userPreferences = ProfilePreferences()

    override suspend fun updateValue(profilePreferences: ProfilePreferences) {
        this.userPreferences = profilePreferences
    }

    override suspend fun obtainValue() = userPreferences
}
