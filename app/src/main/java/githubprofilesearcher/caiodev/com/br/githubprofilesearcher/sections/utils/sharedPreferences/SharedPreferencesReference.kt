package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.sharedPreferences

import android.content.Context

object SharedPreferencesReference {

    fun getSharedPreferencesReference(context: Context, name: String, mode: Int) =
        context.getSharedPreferences(name, mode)
}