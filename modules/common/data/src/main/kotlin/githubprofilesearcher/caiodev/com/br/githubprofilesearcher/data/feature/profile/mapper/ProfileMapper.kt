package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.mapper

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.entity.ProfileEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.model.ProfileModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfileModel

internal fun ProfileModel.mapFromRemote(): UserProfileModel =
    UserProfileModel(
        profileList =
        profileList.map {
            UserProfile(
                login = it.login,
                profileUrl = it.profileUrl,
                profileId = it.profileId,
                profileImage = it.profileImage,
            )
        },
    )

internal fun List<ProfileEntity>.mapFromEntity(): List<UserProfile> =
    map {
        UserProfile(
            login = it.login,
            profileUrl = it.profileUrl,
            profileId = it.profileId,
            profileImage = it.profileImage,
        )
    }

internal fun List<UserProfile>.mapToEntity(): List<ProfileEntity> =
    map {
        ProfileEntity(
            login = it.login,
            profileUrl = it.profileUrl,
            profileId = it.profileId,
            profileImage = it.profileImage,
        )
    }
