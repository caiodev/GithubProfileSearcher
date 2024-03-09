package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.state

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile

data class ProfileState(
    val isSuccess: Boolean = false,
    val isSuccessWithContent: Boolean = false,
    val areAllResultsEmpty: Boolean = false,
    val successMessage: Int = 0,
    val errorMessage: Int = 0,
    val content: List<UserProfile> = emptyList(),
)
