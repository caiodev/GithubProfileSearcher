package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile

@Dao
interface ProfileDao {

    @Query("SELECT * " + "FROM UserProfile LIMIT 20")
    suspend fun getProfilesFromDb(): List<UserProfile>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)

    @Query(value = "DELETE FROM UserProfile")
    suspend fun dropProfileInformation()
}
