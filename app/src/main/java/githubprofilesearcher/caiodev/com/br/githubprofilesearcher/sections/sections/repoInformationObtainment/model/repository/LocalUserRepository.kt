package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.repository

import android.content.SharedPreferences
import androidx.core.content.edit

class LocalUserRepository {

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