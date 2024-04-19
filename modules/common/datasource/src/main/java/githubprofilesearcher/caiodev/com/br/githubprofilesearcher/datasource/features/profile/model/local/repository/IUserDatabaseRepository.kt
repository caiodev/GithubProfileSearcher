package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database.UserDao

interface IUserDatabaseRepository : UserDao {
    override suspend fun get(): List<User>

    override suspend fun upsert(profileList: List<User>)

    override suspend fun drop()
}
