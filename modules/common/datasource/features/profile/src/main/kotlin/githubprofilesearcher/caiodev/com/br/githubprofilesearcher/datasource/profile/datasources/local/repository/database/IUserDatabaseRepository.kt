package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.dao.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity

interface IUserDatabaseRepository : User {
    override suspend fun get(): List<UserEntity>

    override suspend fun upsert(profileList: List<UserEntity>)

    override suspend fun drop()
}
