package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager

interface IProtoDataStoreManager {
    suspend fun <T> obtainValue(): T
    suspend fun <T> updateValue(data: T)
}
