package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.state

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

data class ProfileUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isSuccessWithContent: Boolean = false,
    val successMessage: Int = Core.string.successful_operation,
    val isEmptyStateError: Boolean = false,
    val errorMessage: Int = Core.string.generic,
    val content: List<UserProfile> = emptyList(),
)
