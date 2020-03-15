package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubProfilesList(
    @SerialName("items") val githubProfileInformationList: List<GithubProfileInformation>
)