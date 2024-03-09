package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database.UserDao

@androidx.room.Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase(), Database {
    abstract override fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}
