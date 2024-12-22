package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.local.database

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.dao.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.entity.ProfileEntity

@androidx.room.Database(entities = [ProfileEntity::class], version = 1)
abstract class AppDatabase :
    RoomDatabase(),
    Database {
    abstract override fun profile(): Profile

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}
