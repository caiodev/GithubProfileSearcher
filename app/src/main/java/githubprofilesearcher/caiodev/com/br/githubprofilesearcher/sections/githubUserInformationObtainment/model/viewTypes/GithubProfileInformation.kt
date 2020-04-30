package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubProfileInformation(
    val login: String = "",
    @SerialName("html_url") val profileUrl: String = "",
    @SerialName("id") val userId: Long = 0,
    @SerialName("avatar_url") val userImage: String = ""
)