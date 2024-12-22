package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.entity.ProfileEntity

@Dao
interface Profile {
    @Query("SELECT * " + "FROM Profile LIMIT 20")
    suspend fun get(): List<ProfileEntity>

    @Insert
    suspend fun insert(profileList: List<ProfileEntity>)

    @Upsert
    suspend fun upsert(profileList: List<ProfileEntity>)

    @Query("DELETE FROM Profile")
    suspend fun drop()
}
