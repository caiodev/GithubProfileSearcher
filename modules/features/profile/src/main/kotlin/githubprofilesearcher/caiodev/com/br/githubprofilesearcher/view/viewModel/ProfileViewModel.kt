package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.onCoroutineStatusRequested
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.state.ProfileUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ProfileViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val aggregator: IProfileDataCellAggregator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState>
        get() = _uiState

    fun getData(profile: String) {
        handleCall(profile = profile, shouldListBeCleared = true)
    }

    fun paginateData() {
        handleCall(shouldListBeCleared = false)
    }

    private fun handleCall(
        profile: String = emptyString(),
        shouldListBeCleared: Boolean = false,
    ) {
        runTaskOnBackground(dispatcher) {
            val profileState =
                aggregator.obtainProfileDataList(
                    profile = profile,
                    shouldListBeCleared = shouldListBeCleared,
                    isCoroutineActive = { onCoroutineStatusRequested() },
                )
            val profileUIState =
                ProfileUIState(
                    isSuccess = profileState.isSuccess,
                    isSuccessWithContent = profileState.isSuccessWithContent,
                    successMessage = profileState.successMessage,
                    isEmptyStateError = profileState.areAllResultsEmpty,
                    errorMessage = profileState.errorMessage,
                    content = profileState.content,
                )
            _uiState.emit(profileUIState)
        }
    }
}
