package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.local.fakes.protoDataStore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.model.ProfilePreferences

class FakeKeyValueStorageManager : IKeyValueStorageManager {

    private var userPreferences = ProfilePreferences()

    override suspend fun updateValue(profilePreferences: ProfilePreferences) {
        this.userPreferences = profilePreferences
    }

    override suspend fun obtainValue() = userPreferences
}
