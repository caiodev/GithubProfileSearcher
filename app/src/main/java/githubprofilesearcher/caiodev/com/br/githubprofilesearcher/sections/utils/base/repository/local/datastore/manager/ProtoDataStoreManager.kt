package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager

import androidx.datastore.core.DataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences
import kotlinx.coroutines.flow.first

class ProtoDataStoreManager(private val protoDataStore: DataStore<UserPreferences>) : IProtoDataStoreManager {

    override suspend fun updateDataStoreValue(userPreferences: UserPreferences) {
        protoDataStore.updateData { userPreferences }
    }

    override suspend fun obtainDataStoreValue() = protoDataStore.data.first()

    companion object {
        const val protoFileName = "settings.proto"
    }
}
