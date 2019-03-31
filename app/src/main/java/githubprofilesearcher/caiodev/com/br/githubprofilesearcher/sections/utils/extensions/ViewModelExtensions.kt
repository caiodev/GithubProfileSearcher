package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel

@Suppress("UNUSED")
internal fun ViewModel.insertValueIntoSharedPreferences(
    sharedPreferences: SharedPreferences,
    key: String,
    value: String
) {
    sharedPreferences.edit {
        putString(key, value)
    }
}

@Suppress("UNUSED")
internal fun ViewModel.getValueFromSharedPreferences(
    sharedPreferences: SharedPreferences,
    key: String,
    value: String? = null
): String? {
    return if (sharedPreferences.contains(key))
        sharedPreferences.getString(key, value)
    else ""
}

@Suppress("UNUSED")
internal inline fun <reified T> ViewModel.castAttributeThroughViewModel(attribute: Any?) =
    attribute as T