package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile

interface IProfileDatabaseRepository {
    suspend fun get(): List<UserProfile>

    suspend fun insert(profileList: List<UserProfile>)

    suspend fun upsert(profileList: List<UserProfile>)

    suspend fun drop()
}
