package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.dataStore.manager

import androidx.datastore.core.DataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castToNonNullable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.local.dataStore.manager.IKeyValueStorageManager
import kotlinx.coroutines.flow.first

class KeyValueStorageManager<S>(private val keyValueStorageClient: DataStore<S>) :
    IKeyValueStorageManager {

    override suspend fun <T> obtainData() = castToNonNullable<T>(keyValueStorageClient.data.first())

    override suspend fun <T> updateData(data: T) {
        keyValueStorageClient.updateData {
            castToNonNullable(data)
        }
    }
}
