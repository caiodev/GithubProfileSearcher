package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model

import com.squareup.moshi.Json
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation

data class GithubProfilesList(
    @field:Json(name = "items") val githubProfileInformationList: MutableList<GithubProfileInformation>
)