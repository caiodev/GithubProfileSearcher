package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.dao.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity

@androidx.room.Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(), Database {
    abstract override fun user(): User

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}
