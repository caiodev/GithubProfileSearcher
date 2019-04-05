package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.projectKeys

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesReference {

    fun getSharedPreferencesReference(
        context: Context,
        name: String,
        mode: Int
    ): SharedPreferences? =
        context.getSharedPreferences(name, mode)
}
