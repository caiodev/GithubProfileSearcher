package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation

@Dao
interface GithubProfilesDao {

    @Query("SELECT * " + "FROM UserProfileInformation LIMIT 20")
    suspend fun getProfilesFromDb(): List<UserProfileInformation>?

    @Insert
    suspend fun insertProfilesIntoDb(githubProfilesList: List<UserProfileInformation>)

    @Delete(entity = UserProfileInformation::class)
    suspend fun dropProfileInformationTable(githubProfilesList: List<UserProfileInformation>)
}
