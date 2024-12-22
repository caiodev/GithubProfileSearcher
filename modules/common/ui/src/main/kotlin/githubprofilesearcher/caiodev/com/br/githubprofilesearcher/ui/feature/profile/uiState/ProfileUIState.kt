package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.uiState

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources

data class ProfileUIState(
    val content: List<UserProfile> = emptyList(),
    val errorMessage: Int = Resources.string.generic,
    val isEmptyStateError: Boolean = content.isEmpty(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isSuccessWithContent: Boolean = isSuccess && content.isNotEmpty(),
    val successMessage: Int = Resources.string.successful_operation,
)
