package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao

@androidx.room.Database(entities = [UserProfileInformation::class], version = 1)
abstract class AppDatabase :
    RoomDatabase(),
    githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database {

    abstract override fun githubProfilesDao(): GithubProfilesDao

    companion object {
        const val databaseName = "app-db"
    }
}
