package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class UserDatabaseRepository(
    private val dispatcher: CoroutineContext,
    private val appDatabase: Database,
) : IUserDatabaseRepository {
    override suspend fun get(): List<UserEntity> =
        withContext(dispatcher) {
            appDatabase.user().get() ?: emptyList()
        }

    override suspend fun insert(profileList: List<UserEntity>) {
        withContext(dispatcher) {
            appDatabase.user().insert(profileList)
        }
    }

    override suspend fun upsert(profileList: List<UserEntity>) {
        withContext(dispatcher) {
            appDatabase.user().upsert(profileList)
        }
    }

    override suspend fun drop() {
        withContext(dispatcher) {
            appDatabase.user().drop()
        }
    }
}
