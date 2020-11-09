package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences

interface IProtoDataStoreManager {
    suspend fun updateDataStoreValue(userPreferences: UserPreferences)
    suspend fun obtainDataStoreValue(): UserPreferences
}
