package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User

@Dao
interface UserDao {
    @Query("SELECT * " + "FROM User LIMIT 20")
    suspend fun get(): List<User>?

    @Upsert
    suspend fun upsert(profileList: List<User>)

    @Query(value = "DELETE FROM User")
    suspend fun drop()
}
