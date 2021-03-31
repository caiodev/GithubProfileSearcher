package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first

@Suppress("UNCHECKED_CAST")
class ProtoDataStoreManager<A>(private val protoDataStore: DataStore<A>) : IProtoDataStoreManager {

    override suspend fun <T> obtainValue() = protoDataStore.data.first() as T

    override suspend fun <T> updateValue(data: T) {
        protoDataStore.updateData { data as A }
    }

    companion object {
        const val profilePreferencesProtoFileName = "profilePreferences.proto"
    }
}
