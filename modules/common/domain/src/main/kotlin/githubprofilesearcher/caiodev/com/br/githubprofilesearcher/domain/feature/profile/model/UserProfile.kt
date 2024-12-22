package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString

data class UserProfile(
    val login: String = emptyString(),
    val profileUrl: String = emptyString(),
    val profileId: Long = defaultLong(),
    val profileImage: String = emptyString(),
)
