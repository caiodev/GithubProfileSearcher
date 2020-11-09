package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.utils.base.repository.local.fakes.protoDataStore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager.IProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences

class FakeProtoDataStoreManager : IProtoDataStoreManager {

    private var userPreferences = UserPreferences()

    override suspend fun updateDataStoreValue(userPreferences: UserPreferences) {
        this.userPreferences = userPreferences
    }

    override suspend fun obtainDataStoreValue() = userPreferences
}
