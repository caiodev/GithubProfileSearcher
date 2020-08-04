package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubProfilesList(
    @SerialName("items") val githubProfileInformationList: List<GithubProfileInformation>
)