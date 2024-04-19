package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserDatabaseRepository(
    private val dispatcher: CoroutineDispatcher,
    private val appDatabase: Database,
) : IUserDatabaseRepository {
    override suspend fun get(): List<User> =
        withContext(dispatcher) {
            appDatabase.userDao().get() ?: emptyList()
        }

    override suspend fun upsert(profileList: List<User>) {
        withContext(dispatcher) {
            appDatabase.userDao().upsert(profileList)
        }
    }

    override suspend fun drop() {
        withContext(dispatcher) {
            appDatabase.userDao().drop()
        }
    }
}
