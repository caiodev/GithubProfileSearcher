package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation

@Dao
interface GithubProfilesDao {

    @Query("SELECT * " + "FROM GithubProfileInformation LIMIT 20")
    suspend fun getGithubProfilesFromDb(): List<GithubProfileInformation>?

    @Insert
    suspend fun insertGithubProfilesIntoDb(githubProfilesList: List<GithubProfileInformation>)

    @Delete(entity = GithubProfileInformation::class)
    suspend fun dropGithubProfileInformationTable(githubProfilesList: List<GithubProfileInformation>)
}
