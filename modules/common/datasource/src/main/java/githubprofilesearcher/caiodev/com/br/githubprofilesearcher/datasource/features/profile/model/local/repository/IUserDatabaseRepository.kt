package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database.UserDao

interface IUserDatabaseRepository : UserDao {
    override suspend fun getUsers(): List<User>

    override suspend fun insertUsers(profileList: List<User>)

    override suspend fun dropUserInfo()
}
