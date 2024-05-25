package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.mutableSharedFlowOf
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.onCoroutineStatusRequested
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.uiState.ProfileUIState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.uiState.mapToProfileUIState
import kotlinx.coroutines.flow.SharedFlow
import kotlin.coroutines.CoroutineContext

internal class ProfileViewModel(
    private val dispatcher: CoroutineContext,
    private val aggregator: IProfileDataCellAggregator,
) : ViewModel() {
    private val _uiState = mutableSharedFlowOf<ProfileUIState>()
    val uiState: SharedFlow<ProfileUIState>
        get() = _uiState

    fun getData(profile: String) {
        handleCall(profile = profile, shouldListBeCleared = true)
    }

    fun paginateData() {
        handleCall(shouldListBeCleared = false)
    }

    private fun handleCall(
        profile: String = emptyString(),
        shouldListBeCleared: Boolean,
    ) {
        runTaskOnBackground(dispatcher) {
            aggregator.obtainProfileDataList(
                profile = profile,
                shouldListBeCleared = shouldListBeCleared,
                isCoroutineActive = { onCoroutineStatusRequested() },
            ).mapToProfileUIState()
                .also { uiState -> _uiState.emit(uiState) }
        }
    }
}
