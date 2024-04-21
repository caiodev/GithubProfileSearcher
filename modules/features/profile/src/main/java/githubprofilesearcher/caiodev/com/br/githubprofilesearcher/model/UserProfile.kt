package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString

data class UserProfile(
    val login: String = emptyString(),
    val profileUrl: String = emptyString(),
    val userId: Long = defaultLong(),
    val userImage: String = emptyString(),
)
