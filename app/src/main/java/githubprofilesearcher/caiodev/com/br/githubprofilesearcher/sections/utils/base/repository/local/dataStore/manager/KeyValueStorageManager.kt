package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

import androidx.datastore.core.DataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import kotlinx.coroutines.flow.first

@Suppress("UNCHECKED_CAST")
class KeyValueStorageManager<A>(private val protoDataStore: DataStore<A>) :
    IKeyValueStorageManager {

    override suspend fun <T> obtainData() = castValue<T>(protoDataStore.data.first())

    override suspend fun <T> updateData(data: T) {
        protoDataStore.updateData {
            castValue(data)
        }
    }

    companion object {
        const val profilePreferencesProtoFileName = "profile.proto"
    }
}
