package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileModel(
    @SerialName("items") val profileList: List<Profile>,
)
