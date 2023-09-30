package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.getValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.setValue

internal class ProfileKeyValueRepository(
    private val keyValueInstance: DataStore<Preferences>,
) : IKeyValueRepository {
    override suspend fun <T> getValue(key: Enum<*>): T = keyValueInstance.getValue(key = key.toString())

    override suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) = keyValueInstance.setValue(key = key.toString(), value = value)

    companion object {
        const val PROFILE_PREFERENCES_INSTANCE_ID = "profile"
    }
}
