package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.fakes.sharedPreferences

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {

    val fakeEditor = FakeEditor()

    override fun contains(key: String?): Boolean {
        TODO("Not yet implemented")
    }
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }
    override fun getAll(): MutableMap<String, *> {
        TODO("Not yet implemented")
    }
    override fun getLong(key: String?, defValue: Long): Long {
        TODO("Not yet implemented")
    }
    override fun getFloat(key: String?, defValue: Float): Float {
        TODO("Not yet implemented")
    }
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        TODO("Not yet implemented")
    }
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun edit(): SharedPreferences.Editor = fakeEditor

    @Suppress("UNUSED")
    fun edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = fakeEditor
        action(editor)
        if (commit) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean) = fakeEditor.fakeSharedPreferencesModel.boolean

    override fun getInt(key: String?, defValue: Int) = fakeEditor.fakeSharedPreferencesModel.integer

    override fun getString(key: String?, defValue: String?) = fakeEditor.fakeSharedPreferencesModel.string
}