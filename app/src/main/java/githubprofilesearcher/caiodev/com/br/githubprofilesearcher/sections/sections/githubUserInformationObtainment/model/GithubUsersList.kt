package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model

import com.squareup.moshi.Json
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubUserInformation

data class GithubUsersList(
    @field:Json(name = "items") val githubUsersInformationList: MutableList<GithubUserInformation>
)