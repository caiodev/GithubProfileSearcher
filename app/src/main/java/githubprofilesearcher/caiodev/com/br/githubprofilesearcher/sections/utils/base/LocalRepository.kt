package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base

import android.content.Context
import android.content.SharedPreferences

open class LocalRepository {

    fun getSharedPreferencesReference(
        context: Context,
        name: String,
        mode: Int
    ): SharedPreferences? =
        context.getSharedPreferences(name, mode)
}