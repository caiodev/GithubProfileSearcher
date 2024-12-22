package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.ui.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IFetchLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IFetchRemoteProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.viewmodel.mutableSharedFlowOf
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.viewmodel.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.feature.profile.uiState.ProfileUIState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.string.emptyString
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.CoroutineContext
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources

internal class ProfileViewModel(
    private val dispatcher: CoroutineContext,
    private val fetchLocalProfileUseCase: IFetchLocalProfileUseCase,
    private val fetchRemoteProfileUseCase: IFetchRemoteProfileUseCase,
) : ViewModel() {
    private val _uiState = mutableSharedFlowOf<ProfileUIState>()
    val uiState: SharedFlow<ProfileUIState>
        get() = _uiState.asSharedFlow()

    fun getData(profile: String) {
        encapsulateCall(profile = profile)
    }

    private fun encapsulateCall(profile: String = emptyString()) {
        runTaskOnBackground(dispatcher) {
            handleCall(profile = profile)
        }
    }

    private suspend fun handleCall(profile: String) {
        fetchRemoteProfileUseCase(
            profile,
            true,
        ).let { (success, error) ->
            success.collect { userProfileModel ->
                userProfileModel?.let {
                    if (it.profileList.isNotEmpty()) {
                        emitUIState(
                            content =
                            fetchLocalProfileUseCase(
                                userProfileModel = userProfileModel,
                                shouldListBeCleared = true,
                            ),
                            isSuccess = true,
                        )
                    } else {
                        emitUIState(errorMessage = Resources.string.client_side)
                    }
                } ?: run {
                    emitUIState()
                }
            }
            error.collect { errorMessage ->
                emitUIState(errorMessage = errorMessage)
            }
        }
    }

    private suspend fun emitUIState(
        content: List<UserProfile> = emptyList(),
        errorMessage: Int = Resources.string.generic,
        isSuccess: Boolean = false,
    ) {
        val stateContent =
            if (
                content.isEmpty() &&
                _uiState.replayCache.isNotEmpty()
            ) {
                _uiState.replayCache.first().content
            } else {
                content
            }
        _uiState.emit(
            ProfileUIState(
                content = stateContent,
                errorMessage = errorMessage,
                isSuccess = isSuccess,
            ),
        )
    }
}
