package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castToNonNullable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.bool.obtainDefaultBoolean
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.obtainDefaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Suppress("UNCHECKED_CAST")
suspend fun <T> DataStore<Preferences>.getValue(key: String): T {
    return when {
        key.contains(GlobalKeyValueIDs.Text.toString()) ->
            provideRequestedValue(
                key = castToNonNullable(stringPreferencesKey(key)),
                fallbackValue = emptyString() as T,
            )

        key.contains(GlobalKeyValueIDs.Status.toString()) ->
            provideRequestedValue(
                key = castToNonNullable(booleanPreferencesKey(key)),
                fallbackValue = obtainDefaultBoolean() as T,
            )

        else ->
            provideRequestedValue(
                key = castToNonNullable(intPreferencesKey(key)),
                fallbackValue = obtainDefaultInteger() as T,
            )
    }
}

private suspend fun <T> DataStore<Preferences>.provideRequestedValue(
    key: Preferences.Key<T>,
    fallbackValue: T,
): T = data.map { preferences -> preferences[key] ?: fallbackValue }.first()

suspend fun <T> DataStore<Preferences>.setValue(
    key: String,
    value: T,
) {
    when (value) {
        is Boolean -> applyKeyValuePersistence(key = booleanPreferencesKey(key), value = value)
        is Int -> applyKeyValuePersistence(key = intPreferencesKey(key), value = value)
        is String -> applyKeyValuePersistence(key = stringPreferencesKey(key), value = value)
        else -> Unit
    }
}

private suspend inline fun <reified T> DataStore<Preferences>.applyKeyValuePersistence(
    key: Preferences.Key<T>,
    value: T,
) {
    edit { profile -> profile[key] = value }
}
