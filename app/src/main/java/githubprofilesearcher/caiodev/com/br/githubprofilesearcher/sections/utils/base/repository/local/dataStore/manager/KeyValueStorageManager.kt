package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

import androidx.datastore.core.DataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castToNonNullable
import kotlinx.coroutines.flow.first

@Suppress("UNCHECKED_CAST")
class KeyValueStorageManager<S>(private val keyValueStorageClient: DataStore<S>) :
    IKeyValueStorageManager {

    override suspend fun <T> obtainData() = castToNonNullable<T>(keyValueStorageClient.data.first())

    override suspend fun <T> updateData(data: T) {
        keyValueStorageClient.updateData {
            castToNonNullable(data)
        }
    }
}
