package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes

import com.squareup.moshi.Json
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfileCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType

data class GithubProfileInformation(
    @field:Json(name = "id") val userId: Long = 0,
    @field:Json(name = "login") val login: String = "",
    @field:Json(name = "avatar_url") val userImage: String = "",
    @field:Json(name = "html_url") val profileUrl: String = "",
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "score") val score: Double?,
    @field:Json(name = "bio") val bio: String?,
    @field:Json(name = "followers") val numberOfFollowers: Int = 0,
    @field:Json(name = "public_repos") val numberOfRepositories: Int = 0
) : ViewType {
    override fun provideViewType() = githubProfileCell
}