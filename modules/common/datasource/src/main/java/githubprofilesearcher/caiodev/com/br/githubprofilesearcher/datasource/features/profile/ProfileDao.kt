package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ProfileDao {
    @androidx.room.Query("SELECT * " + "FROM UserProfile LIMIT 20")
    suspend fun getProfilesFromDb(): List<UserProfile>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)

    @androidx.room.Query(value = "DELETE FROM UserProfile")
    suspend fun dropProfileInformation()
}
