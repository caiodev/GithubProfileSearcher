package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile

@Dao
interface ProfileDao {

    @Query("SELECT * " + "FROM UserProfile LIMIT 20")
    suspend fun getProfilesFromDb(): List<UserProfile>?

    @Insert
    suspend fun insertProfilesIntoDb(profileList: List<UserProfile>)

    @Delete(entity = UserProfile::class)
    suspend fun dropProfileInformationTable(profileList: List<UserProfile>)
}
