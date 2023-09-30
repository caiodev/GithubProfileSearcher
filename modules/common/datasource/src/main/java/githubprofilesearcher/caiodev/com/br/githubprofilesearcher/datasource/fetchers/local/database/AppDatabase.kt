package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile

@androidx.room.Database(entities = [UserProfile::class], version = 1)
abstract class AppDatabase : RoomDatabase(), Database {
    abstract override fun profileDao(): ProfileDao

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}
