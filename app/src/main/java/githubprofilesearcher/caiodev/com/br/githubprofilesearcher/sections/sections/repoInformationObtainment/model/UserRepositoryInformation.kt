package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model

import com.squareup.moshi.Json

data class UserRepositoryInformation(
    @field:Json(name = "id") val userId: String = "",
    @field:Json(name = "avatar_url") val userImage: String = "",
    @field:Json(name = "html_url") val profileUrl: String = "",
    @field:Json(name = "name") val name: String? = "",
    @field:Json(name = "bio") val bio: String? = "",
    @field:Json(name = "followers") val numberOfFollowers: Int = 0,
    @field:Json(name = "public_repos") val numberOfRepositories: Int = 0
)