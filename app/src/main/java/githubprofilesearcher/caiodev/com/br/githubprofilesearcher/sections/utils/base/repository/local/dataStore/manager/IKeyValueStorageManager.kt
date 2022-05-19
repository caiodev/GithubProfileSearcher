package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

interface IKeyValueStorageManager {
    suspend fun <T> obtainData(): T
    suspend fun <T> updateData(data: T)
}
