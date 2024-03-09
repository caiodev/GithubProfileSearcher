package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.state

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile

data class ProfileUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isSuccessWithContent: Boolean = false,
    val successMessage: Int = 0,
    val isEmptyStateError: Boolean = false,
    val errorMessage: Int = 0,
    val content: List<UserProfile> = emptyList(),
)
