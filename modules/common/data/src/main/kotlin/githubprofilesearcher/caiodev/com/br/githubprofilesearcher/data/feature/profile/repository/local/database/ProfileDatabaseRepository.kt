package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.mapper.mapFromEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.mapper.mapToEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ProfileDatabaseRepository(
    private val dispatcher: CoroutineContext,
    private val appDatabase: Database,
) : IProfileDatabaseRepository {
    override suspend fun get(): List<UserProfile> =
        withContext(dispatcher) {
            appDatabase.profile().get().mapFromEntity()
        }

    override suspend fun insert(profileList: List<UserProfile>) {
        withContext(dispatcher) {
            appDatabase.profile().insert(profileList.mapToEntity())
        }
    }

    override suspend fun upsert(profileList: List<UserProfile>) {
        withContext(dispatcher) {
            appDatabase.profile().upsert(profileList.mapToEntity())
        }
    }

    override suspend fun drop() {
        withContext(dispatcher) {
            appDatabase.profile().drop()
        }
    }
}
