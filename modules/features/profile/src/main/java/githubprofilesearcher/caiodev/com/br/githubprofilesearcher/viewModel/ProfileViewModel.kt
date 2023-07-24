package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.ActionNotRequired
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.InitialIntermediate
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.InitialSuccess
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Intermediate
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.LocalPopulation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.extensions.runTaskOnForeground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.obtainDefaultString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.ProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.CallStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.CurrentProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.DeletedProfileStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.LastAttemptStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.LocalPopulationStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.PageNumber
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.ProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.SearchStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.SuccessStatus
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs.TemporaryProfileText
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class ProfileViewModel(
    private val profileDataAggregator: ProfileDataAggregator,
    private val networkChecking: NetworkChecking,
    private val profileDatabaseRepository: IProfileDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : ViewModel() {

    private val _successStateFlow = MutableStateFlow<State<Success>>(InitialSuccess)
    val successStateFlow: StateFlow<State<Success>>
        get() = _successStateFlow

    private val _intermediateSharedFlow = MutableSharedFlow<State<Intermediate>>()
    val intermediateSharedFlow = _intermediateSharedFlow.asSharedFlow()

    private val _errorSharedFlow = MutableSharedFlow<State<Error>>()
    val errorSharedFlow = _errorSharedFlow.asSharedFlow()

    private val _profileInfoList = mutableListOf<UserProfile>()
    private var profilesInfoList: List<UserProfile> = _profileInfoList

    private var currentIntermediateState: State<Intermediate> = InitialIntermediate

    fun requestUpdatedProfiles(profile: String = obtainDefaultString()) {
        setValue(key = PageNumber, value = initialPage)

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
            shouldListItemsBeRemoved = false
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
            val value =
                profileOriginRepository.provideUserInformation(
                    user = user,
                    pageNumber = getValue(key = PageNumber),
                    maxResultsPerPage = itemsPerPage,
                )
            handleResult(value, shouldListItemsBeRemoved)
        }
    }

    private suspend fun handleResult(value: Any, shouldListItemsBeRemoved: Boolean) {
        when (value) {

            is SuccessWithBody<*> -> {

                setValue(key = CurrentProfileText, value = obtainDefaultString())

                if (!getValue<Boolean>(SuccessStatus))
                    setValue(key = SuccessStatus, value = true)

                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(value)
                } else {
                    setupPaginationList(successWithBody = value)
                }
            }

            is SuccessWithoutBody -> Unit

            else -> {
                handleError(ValueCasting.castTo(value))
            }
        }
    }

    private suspend fun handleError(error: State<Error>?) {
        error?.let {
            _errorSharedFlow.emit(error)
        }
    }

    private fun setupUpdatedList(
        successWithBody: SuccessWithBody<*>,
    ) {
        runTaskOnBackground {
            successWithBody.apply {
                profileDatabaseRepository.dropProfileInformation()
                if (_profileInfoList.isNotEmpty()) {
                    _profileInfoList.clear()
                }
                castTo<Profile>(successWithBody.data)?.let { profile ->
                    addContentToProfileInfoList(profile.profile)
                    profileDatabaseRepository.insertProfilesIntoDb(
                        profile.profile,
                    )
                }
                saveDataAfterSuccess(this)
            }
        }
    }

    private fun setupPaginationList(
        shouldSavedListBeUsed: Boolean = false,
        successWithBody: SuccessWithBody<*> = SuccessWithBody(Any()),
    ) {
        runTaskOnBackground {
            successWithBody.apply {
                if (!shouldSavedListBeUsed) {
                    castTo<Profile>(successWithBody.data)?.let {
                        addContentToProfileInfoList(it.profile)
                    }
                    profileDatabaseRepository.insertProfilesIntoDb(profilesInfoList)
                } else {
                    addContentToProfileInfoList(profileDatabaseRepository.getProfilesFromDb())
                }
                saveDataAfterSuccess(this)
            }
        }
    }

    fun <T> getValue(key: Enum<*>): T {
        return runTaskOnForeground { profileDataAggregator.getValue(key = key) }
    }

    fun <T> setValue(key: Enum<*>, value: T) {
        runTaskOnForeground { profileDataAggregator.setValue(key = key, value = value) }
    }

    private fun addContentToProfileInfoList(list: List<UserProfile>) {
        _profileInfoList.addAll(list)
    }

    fun updateUIWithCache() {
        setupPaginationList(shouldSavedListBeUsed = true)
    }

    fun obtainConnectionState() = networkChecking.checkIfConnectionIsAvailable()

    fun provideConnectionObserver() = networkChecking.obtainConnectionObserver()

    private suspend fun saveDataAfterSuccess(successWithBody: SuccessWithBody<*>) {
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

        setValue(key = PageNumber, getValue<Int>(key = PageNumber).plus(initialPage))

        _successStateFlow.emit(InitialSuccess)
        _successStateFlow.emit(
            SuccessWithBody(
                data = profilesInfoList,
                successWithBody.totalPages,
            ),
        )
    }

    fun checkDataAtStartup() {
        runTaskOnBackground {
            if (successStateFlow.value == InitialSuccess &&
                profileDatabaseRepository.getProfilesFromDb().isEmpty()
            ) {
                postIntermediateState(ActionNotRequired)
            } else if (profileDatabaseRepository.getProfilesFromDb().isNotEmpty() &&
                profilesInfoList.isEmpty()
            ) {
                setValue(key = LocalPopulationStatus, value = true)
                setValue(key = SuccessStatus, value = false)
                postIntermediateState(LocalPopulation)
            }
        }
    }

    private suspend fun postIntermediateState(state: State<Intermediate>) {
        if (state != currentIntermediateState) {
            currentIntermediateState = state
            _intermediateSharedFlow.emit(state)
        }
    }

    inline fun <reified T> castTo(value: Any) = ValueCasting.castTo<T>(value)

    private companion object {
        const val initialPage = 1
        const val itemsPerPage = 20
    }
}
