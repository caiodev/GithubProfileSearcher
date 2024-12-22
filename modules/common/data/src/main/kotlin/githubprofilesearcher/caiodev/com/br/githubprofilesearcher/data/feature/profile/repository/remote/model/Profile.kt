package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("login") val login: String = emptyString(),
    @SerialName("html_url") val profileUrl: String = emptyString(),
    @SerialName("id") val profileId: Long = defaultLong(),
    @SerialName("avatar_url") val profileImage: String = emptyString(),
)
