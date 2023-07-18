package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager

interface IKeyValueStorageManager {
    suspend fun <T> obtainData(): T
    suspend fun <T> updateData(data: T)
}
