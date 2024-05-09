package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.number.defaultInteger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.UserProfile

data class ProfileState(
    val isSuccess: Boolean = false,
    val isSuccessWithContent: Boolean = false,
    val areAllResultsEmpty: Boolean = false,
    val successMessage: Int = defaultInteger(),
    val errorMessage: Int = defaultInteger(),
    val content: List<UserProfile> = emptyList(),
)
