package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity

@Dao
interface User {
    @Query("SELECT * " + "FROM User LIMIT 20")
    suspend fun get(): List<UserEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profileList: List<UserEntity>)

    @Upsert
    suspend fun upsert(profileList: List<UserEntity>)

    @Query(value = "DELETE FROM User")
    suspend fun drop()
}
