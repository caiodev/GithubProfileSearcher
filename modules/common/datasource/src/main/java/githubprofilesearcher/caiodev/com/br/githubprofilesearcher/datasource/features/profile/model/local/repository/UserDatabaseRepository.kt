package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserDatabaseRepository(
    private val dispatcher: CoroutineDispatcher,
    private val appDatabase: Database,
) : IUserDatabaseRepository {
    override suspend fun getUsers(): List<User> =
        withContext(dispatcher) {
            appDatabase.userDao().getUsers() ?: emptyList()
        }

    override suspend fun insertUsers(profileList: List<User>) {
        withContext(dispatcher) {
            appDatabase.userDao()
                .insertUsers(profileList)
        }
    }

    override suspend fun dropUserInfo() {
        withContext(dispatcher) {
            appDatabase.userDao().dropUserInfo()
        }
    }
}
