package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultLong
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("login") val login: String = emptyString(),
    @SerialName("html_url") val profileUrl: String = emptyString(),
    @SerialName("id") val userId: Long = defaultLong(),
    @SerialName("avatar_url") val userImage: String = emptyString(),
)
