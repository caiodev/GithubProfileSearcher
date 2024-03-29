package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("items") val profile: List<UserProfile>
)
