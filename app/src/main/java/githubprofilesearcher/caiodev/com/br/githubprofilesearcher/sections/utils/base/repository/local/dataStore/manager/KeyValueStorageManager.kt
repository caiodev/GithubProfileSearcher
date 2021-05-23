package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

import androidx.datastore.core.DataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import kotlinx.coroutines.flow.first

@Suppress("UNCHECKED_CAST")
class KeyValueStorageManager<S>(private val keyValueStorageClient: DataStore<S>) :
    IKeyValueStorageManager {

    override suspend fun <T> obtainData() = castValue<T>(keyValueStorageClient.data.first())

    override suspend fun <T> updateData(data: T) {
        keyValueStorageClient.updateData {
            castValue(data)
        }
    }
}
