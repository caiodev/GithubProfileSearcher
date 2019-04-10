package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory

import android.content.Context
import android.content.SharedPreferences

object SharedPreferences {

    fun getSharedPreferencesReference(
        context: Context,
        name: String,
        mode: Int
    ): SharedPreferences = context.getSharedPreferences(name, mode)
}
