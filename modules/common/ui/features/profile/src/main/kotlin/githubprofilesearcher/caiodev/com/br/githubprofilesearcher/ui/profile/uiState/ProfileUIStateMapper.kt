package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.uiState

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state.ProfileState

internal fun ProfileState.mapToProfileUIState() =
    ProfileUIState(
        isSuccess = isSuccess,
        isSuccessWithContent = isSuccessWithContent,
        successMessage = successMessage,
        isEmptyStateError = areAllResultsEmpty,
        errorMessage = errorMessage,
        content = content,
    )
