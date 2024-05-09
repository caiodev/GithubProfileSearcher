package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.mapper

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.UserProfile

internal fun List<User>.mapFromRemote(): List<UserProfile> =
    map {
        UserProfile(
            login = it.login,
            profileUrl = it.profileUrl,
            userId = it.userId,
            userImage = it.userImage,
        )
    }

internal fun List<UserEntity>.mapFromEntity(): List<UserProfile> =
    map {
        UserProfile(
            login = it.login,
            profileUrl = it.profileUrl,
            userId = it.userId,
            userImage = it.userImage,
        )
    }

internal fun List<User>.mapToEntity(): List<UserEntity> =
    map {
        UserEntity(
            login = it.login,
            profileUrl = it.profileUrl,
            userId = it.userId,
            userImage = it.userImage,
        )
    }
