package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base

import android.content.SharedPreferences
import androidx.core.content.edit

class LocalRepository {

    internal fun insertValueIntoSharedPreferences(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String
    ) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    internal fun getValueFromSharedPreferences(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String? = null
    ): String? {
        return if (sharedPreferences.contains(key))
            sharedPreferences.getString(key, value)
        else ""
    }
}