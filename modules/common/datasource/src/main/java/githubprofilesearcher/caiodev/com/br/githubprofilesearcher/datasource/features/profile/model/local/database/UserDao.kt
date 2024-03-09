package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User

@Dao
interface UserDao {
    @androidx.room.Query("SELECT * " + "FROM User LIMIT 20")
    suspend fun getUsers(): List<User>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(profileList: List<User>)

    @androidx.room.Query(value = "DELETE FROM User")
    suspend fun dropUserInfo()
}
