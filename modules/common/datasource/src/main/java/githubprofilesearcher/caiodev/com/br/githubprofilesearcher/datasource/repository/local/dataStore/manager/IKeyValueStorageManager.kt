package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.dataStore.manager

interface IKeyValueStorageManager {
    suspend fun <T> obtainData(): T
    suspend fun <T> updateData(data: T)
}
