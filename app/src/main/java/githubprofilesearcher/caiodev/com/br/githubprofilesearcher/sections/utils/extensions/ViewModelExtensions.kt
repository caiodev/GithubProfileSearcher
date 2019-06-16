package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.LocalRepository

@Suppress("UNUSED")
internal inline fun <reified T> ViewModel.castAttributeThroughViewModel(attribute: Any?) =
    attribute as T

internal fun insertValueIntoSharedPreferences(
    localRepository: LocalRepository? = null,
    sharedPreferences: SharedPreferences,
    key: String,
    value: String
) {
    localRepository?.insertValueIntoSharedPreferences(sharedPreferences, key, value)
}

internal fun getValueFromSharedPreferences(
    localRepository: LocalRepository? = null,
    sharedPreferences: SharedPreferences,
    key: String,
    value: String? = null
) = localRepository?.getValueFromSharedPreferences(sharedPreferences, key, value)