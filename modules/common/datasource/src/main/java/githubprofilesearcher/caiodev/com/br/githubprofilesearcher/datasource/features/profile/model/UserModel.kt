package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    @SerialName("items") val profileList: List<User>,
)
