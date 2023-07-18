package repository.local.fakes.protoDataStore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager.IKeyValueStorageManager

class FakeKeyValueStorageManager : IKeyValueStorageManager {

    private var userPreferences = ProfilePreferences()

    override suspend fun <T> obtainData(): T = userPreferences

    override suspend fun <T> updateData(data: T) {
        this.userPreferences = profilePreferences
    }
}
