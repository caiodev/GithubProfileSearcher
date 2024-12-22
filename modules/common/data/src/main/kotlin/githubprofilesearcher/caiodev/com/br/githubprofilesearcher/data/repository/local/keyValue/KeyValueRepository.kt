package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.repository.local.keyValue

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.repository.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castToNonNullable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.bool.obtainDefaultBoolean
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.text.contains

@Suppress("UNCHECKED_CAST")
class KeyValueRepository(
    private val keyValueInstance: DataStore<Preferences>,
) : IKeyValueRepository {
    override suspend fun <T> getValue(key: Enum<*>): T =
        when {
            key.name.contains(GlobalKeyValueTypes.Text.name) ->
                keyValueInstance.provideRequestedValue(
                    key = stringPreferencesKey(key.name).castToNonNullable(),
                    fallbackValue = emptyString() as T,
                )

            key.name.contains(GlobalKeyValueTypes.Status.name) ->
                keyValueInstance.provideRequestedValue(
                    key = booleanPreferencesKey(key.name).castToNonNullable(),
                    fallbackValue = obtainDefaultBoolean() as T,
                )

            else ->
                keyValueInstance.provideRequestedValue(
                    key = intPreferencesKey(key.name).castToNonNullable(),
                    fallbackValue = defaultInteger() as T,
                )
        }

    override suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) {
        when (value) {
            is Boolean ->
                keyValueInstance.applyKeyValuePersistence(
                    key = booleanPreferencesKey(key.name),
                    value = value,
                )
            is Int ->
                keyValueInstance.applyKeyValuePersistence(
                    key = intPreferencesKey(key.name),
                    value = value,
                )
            is String ->
                keyValueInstance.applyKeyValuePersistence(
                    key = stringPreferencesKey(key.name),
                    value = value,
                )
            else -> Unit
        }
    }

    private suspend fun <T> DataStore<Preferences>.provideRequestedValue(
        key: Preferences.Key<T>,
        fallbackValue: T,
    ): T = data.map { preferences -> preferences[key] ?: fallbackValue }.first()

    private suspend inline fun <reified T> DataStore<Preferences>.applyKeyValuePersistence(
        key: Preferences.Key<T>,
        value: T,
    ) {
        edit { profile -> profile[key] = value }
    }

    companion object {
        internal const val PREFERENCES_INSTANCE_ID = "preferences"
    }
}
