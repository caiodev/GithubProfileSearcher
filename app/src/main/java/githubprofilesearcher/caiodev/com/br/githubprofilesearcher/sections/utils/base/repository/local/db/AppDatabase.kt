package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao.ProfileDao

@androidx.room.Database(entities = [UserProfile::class], version = 1)
abstract class AppDatabase :
    RoomDatabase(),
    githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database {

    abstract override fun profileDao(): ProfileDao

    companion object {
        const val databaseName = "app-db"
    }
}
