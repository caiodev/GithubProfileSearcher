package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.utils.base.repository.local.fakes.protoDataStore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.model.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.local.dataStore.manager.IKeyValueStorageManager

class FakeKeyValueStorageManager :
    githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.local.dataStore.manager.IKeyValueStorageManager {

    private var userPreferences = ProfilePreferences()

    override suspend fun updateValue(profilePreferences: ProfilePreferences) {
        this.userPreferences = profilePreferences
    }

    override suspend fun obtainValue() = userPreferences
}
