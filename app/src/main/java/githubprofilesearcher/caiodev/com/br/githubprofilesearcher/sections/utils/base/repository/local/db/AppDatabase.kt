package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db

import androidx.room.RoomDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao

@androidx.room.Database(entities = [GithubProfileInformation::class], version = 1)
abstract class AppDatabase :
    RoomDatabase(),
    githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database {
    abstract override fun githubProfilesDao(): GithubProfilesDao
}
