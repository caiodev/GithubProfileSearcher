package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("items") val profile: List<UserProfile>,
)
