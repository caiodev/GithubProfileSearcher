package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.fakes.sharedPreferences

import android.content.SharedPreferences

class FakeEditor : SharedPreferences.Editor {

    var fakeSharedPreferencesModel = FakeSharedPreferencesModel()

    override fun apply() {}
    override fun commit() = true
    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }
    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }
    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }
    override fun remove(key: String?): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun clear(): SharedPreferences.Editor {
        fakeSharedPreferencesModel = FakeSharedPreferencesModel()
        return this
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        fakeSharedPreferencesModel.boolean = value
        return this
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        fakeSharedPreferencesModel.integer = value
        return this
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        fakeSharedPreferencesModel.string = value
        return this
    }
}