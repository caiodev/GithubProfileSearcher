package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubProfileInformation(
    val login: String = "",
    @SerialName("html_url") val profileUrl: String = "",
    @SerialName("id") val userId: Long = 0,
    @SerialName("avatar_url") val userImage: String = ""
) : ViewType {
    override fun provideViewType() = githubProfileCell
}