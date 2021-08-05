package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryInformation(
    @SerialName("id") val repositoryId: String,
    @SerialName("name") val repositoryName: String?,
    @SerialName("html_url") val repositoryUrl: String,
    @SerialName("stargazers_count") val starsCount: String?,
    @SerialName("forks_count") val forksCount: Int
)
