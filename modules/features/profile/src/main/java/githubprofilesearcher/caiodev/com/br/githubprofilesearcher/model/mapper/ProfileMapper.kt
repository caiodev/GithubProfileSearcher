package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.mapper

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile

internal fun List<User>.mapFrom(): List<UserProfile> =
    map {
        UserProfile(
            login = it.login,
            profileUrl = it.profileUrl,
            userId = it.userId,
            userImage = it.userImage,
        )
    }
