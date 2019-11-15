package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model

import com.squareup.moshi.Json

data class GithubProfileRepositoryInformation(
    @field:Json(name = "id") val repositoryId: String,
    @field:Json(name = "name") val repositoryName: String?,
    @field:Json(name = "html_url") val repositoryUrl: String,
    @field:Json(name = "stargazers_count") val starsCount: String?,
    @field:Json(name = "forks_count") val forksCount: Int
)