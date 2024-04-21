package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnForeground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.state.ProfileUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ProfileViewModel(
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
        runTaskOnBackground {
            val profileState =
                aggregator.obtainProfileDataList(
                    profile = profile,
                    shouldListBeCleared = shouldListBeCleared,
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

    fun <T> getValue(key: Enum<*>): T = runTaskOnForeground { aggregator.getValue(key = key) }

    fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) {
        runTaskOnForeground { aggregator.setValue(key = key, value = value) }
    }
}
