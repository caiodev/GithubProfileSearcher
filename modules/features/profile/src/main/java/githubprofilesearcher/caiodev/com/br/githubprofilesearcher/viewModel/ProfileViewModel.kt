package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ErrorWithMessage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnForeground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.obtainDefaultString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.IProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.CallStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.CurrentProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.DeletedProfileStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.LastAttemptStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.PageNumber
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.ProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.SearchStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.TemporaryProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Default
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.Loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.UIState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.states.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

internal class ProfileViewModel(
    private val profileDataAggregator: IProfileDataAggregator,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(Default)
    val uiState: StateFlow<UIState>
        get() = _uiState

    private val _profileInfoList = mutableListOf<UserProfile>()
    private val profilesInfoList: List<UserProfile> = _profileInfoList

    fun requestUpdatedProfiles(profile: String = obtainDefaultString()) {
        setValue(key = PageNumber, value = INITIAL_PAGE)

        if (profile.isNotEmpty()) {
            setValue(key = TemporaryProfileText, value = profile)
            requestProfiles(profile, true)
        } else {
            requestProfiles(
                profile = getValue(TemporaryProfileText),
                shouldListItemsBeRemoved = true,
            )
        }
    }

    fun paginateProfiles() {
        requestProfiles(
            profile = getValue(CurrentProfileText),
            shouldListItemsBeRemoved = false,
        )
    }

    private fun requestProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean,
    ) {
        if (shouldListItemsBeRemoved) {
            handleCall(profile, true)
        } else {
            handleCall(profile, false)
        }
    }

    private fun handleCall(
        user: String,
        shouldListItemsBeRemoved: Boolean = false,
    ) {
        runTaskOnBackground {
            _uiState.emit(Loading)
            val value =
                profileDataAggregator.fetchProfileInfo(
                    user = user,
                    pageNumber = getValue(key = PageNumber),
                    maxResultsPerPage = ITEMS_PER_PAGE,
                )
            handleResult(value, shouldListItemsBeRemoved)
        }
    }

    private suspend fun handleResult(
        value: State<*>,
        shouldListItemsBeRemoved: Boolean,
    ) {
        when (value) {
            is SuccessWithBody<*> -> {
                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(value)
                } else {
                    setupPaginationList(successWithBody = value)
                }
            }

            is ErrorWithMessage -> {
                if (value.message == Core.string.generic) {
                    _uiState.emit(Empty(value.message))
                } else {
                    _uiState.emit(Error(value.message))
                }
            }

            else -> _uiState.emit(Default)
        }
    }

    private fun setupUpdatedList(successWithBody: SuccessWithBody<*>) {
        runTaskOnBackground {
            successWithBody.apply {
                if (_profileInfoList.isNotEmpty()) {
                    _profileInfoList.clear()
                }
                castTo<Profile>(successWithBody.data)?.let { profile ->
                    addContentToProfileInfoList(profile.profile)
                }
                saveDataAfterSuccess()
            }
        }
    }

    private fun setupPaginationList(successWithBody: SuccessWithBody<*> = SuccessWithBody(Any())) {
        runTaskOnBackground {
            successWithBody.apply {
                castTo<Profile>(successWithBody.data)?.let {
                    addContentToProfileInfoList(it.profile)
                }
                saveDataAfterSuccess()
            }
        }
    }

    fun <T> getValue(key: Enum<*>): T {
        return runTaskOnForeground { profileDataAggregator.getValue(key = key) }
    }

    fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) {
        runTaskOnForeground { profileDataAggregator.setValue(key = key, value = value) }
    }

    private fun addContentToProfileInfoList(list: List<UserProfile>) {
        _profileInfoList.addAll(list)
    }

    private suspend fun saveDataAfterSuccess() {
        setValue(key = CurrentProfileText, value = getValue<String>(key = TemporaryProfileText))
        setValue(key = LastAttemptStatus, value = false)
        setValue(key = CallStatus, value = false)

        if (getValue(key = DeletedProfileStatus) &&
            getValue<String>(key = ProfileText).isNotEmpty()
        ) {
            setValue(key = SearchStatus, value = true)
        } else {
            setValue(key = SearchStatus, value = false)
        }

        setValue(key = PageNumber, getValue<Int>(key = PageNumber).plus(INITIAL_PAGE))

        _uiState.emit(User(content = profilesInfoList))
    }

    private companion object {
        const val INITIAL_PAGE = 1
        const val ITEMS_PER_PAGE = 20
    }
}
