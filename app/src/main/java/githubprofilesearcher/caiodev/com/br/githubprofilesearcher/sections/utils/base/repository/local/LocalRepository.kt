package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository

@Suppress("UNCHECKED_CAST")
class LocalRepository(
    private val sharedPreferences: SharedPreferences,
    private val appDatabase: Database
) : GenericLocalRepository {

    override fun <T> retrieveValueFromSharedPreferences(
        key: String,
        defaultValue: T
    ): T {
        when (defaultValue) {
            is Boolean -> return sharedPreferences.getBoolean(key, defaultValue) as T
            is Int -> return sharedPreferences.getInt(key, defaultValue) as T
            is String -> return sharedPreferences.getString(key, defaultValue) as T
        }
        return defaultValue
    }

    override fun <T> saveValueToSharedPreferences(
        key: String,
        value: T
    ) {
        sharedPreferences.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
            }
        }
    }

    override fun clearSharedPreferences() {
        sharedPreferences.edit {
            clear()
        }
    }

    override suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation> =
        appDatabase.githubProfilesDao().getGithubProfilesFromDb()

    override suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>) {
        appDatabase.githubProfilesDao()
            .insertGithubProfilesIntoDb(githubProfilesList)
    }

    override suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>) {
        appDatabase.githubProfilesDao().dropGithubProfileInformationTable(githubProfilesList)
    }
}