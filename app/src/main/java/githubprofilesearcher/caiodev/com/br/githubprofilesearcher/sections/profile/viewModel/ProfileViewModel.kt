package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.serializer.model.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.remote.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ActionNotRequired
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.InitialIntermediate
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.InitialSuccess
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Intermediate
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.LocalPopulation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.SuccessWithoutBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnForeground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class ProfileViewModel(
    private val networkChecking: NetworkChecking,
    private val localRepository: ILocalRepository,
    private val remoteRepository: IProfileRepository
) : ViewModel() {

    private val _successStateFlow = MutableStateFlow<State<Success>>(InitialSuccess)
    internal val successStateFlow: StateFlow<State<Success>>
        get() = _successStateFlow

    private val _intermediateSharedFlow = MutableSharedFlow<State<Intermediate>>()
    internal val intermediateSharedFlow = _intermediateSharedFlow.asSharedFlow()

    private val _errorSharedFlow =
        MutableSharedFlow<State<githubprofilesearcher.caiodev.com.br
        .githubprofilesearcher.sections.utils.base.states.Error>>()
    internal val errorSharedFlow = _errorSharedFlow.asSharedFlow()

    private val _profileInfoList = mutableListOf<UserProfile>()
    private var profilesInfoList: List<UserProfile> = _profileInfoList

    private var currentIntermediateState: State<Intermediate> = InitialIntermediate

    private var profilePreferences: ProfilePreferences = ProfilePreferences.getDefaultInstance()

    fun requestUpdatedProfiles(profile: String = emptyString()) {

        saveValueToDataStore(
            obtainValueFromDataStore().copy(pageNumber = initialPage)
        )

        if (profile.isNotEmpty()) {
            saveValueToDataStore(
                obtainValueFromDataStore().copy(temporaryCurrentProfile = profile)
            )
            requestProfiles(profile, true)
        } else {
            requestProfiles(
                obtainValueFromDataStore().temporaryCurrentProfile,
                true
            )
        }
    }

    fun paginateProfiles() {
        requestProfiles(obtainValueFromDataStore().currentProfile, false)
    }

    private fun requestProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean
    ) {
        if (shouldListItemsBeRemoved)
            handleCall(profile, true)
        else
            handleCall(profile, false)
    }

    private fun handleCall(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {
        runTaskOnBackground {
            val value =
                remoteRepository.provideUserInformation(
                    user,
                    obtainValueFromDataStore().pageNumber,
                    itemsPerPage
                )
            handleResult(value, shouldListItemsBeRemoved)
        }
    }

    private suspend fun handleResult(value: Any, shouldListItemsBeRemoved: Boolean) {
        when (value) {
            is SuccessWithBody<*> -> {
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(currentProfile = emptyString())
                )

                if (!obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                ) {
                    saveValueToDataStore(
                        obtainValueFromDataStore().copy(hasASuccessfulCallAlreadyBeenMade = true)
                    )
                }

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

    private suspend fun handleError(
        error: State<githubprofilesearcher.caiodev.com.br
        .githubprofilesearcher.sections.utils.base.states.Error>?
    ) {
        error?.let {
            _errorSharedFlow.emit(error)
        }
    }

    private fun setupUpdatedList(
        successWithBody: SuccessWithBody<*>
    ) {
        runTaskOnBackground {
            successWithBody.apply {
                localRepository.dropProfileInformation()
                if (_profileInfoList.isNotEmpty()) {
                    _profileInfoList.clear()
                }
                castTo<Profile>(successWithBody.data)?.let { profile ->
                    addContentToProfileInfoList(profile.profile)
                    localRepository.insertProfilesIntoDb(
                        profile.profile
                    )
                }
                saveDataAfterSuccess(this)
            }
        }
    }

    private fun setupPaginationList(
        shouldSavedListBeUsed: Boolean = false,
        successWithBody: SuccessWithBody<*> = SuccessWithBody(Any())
    ) {
        runTaskOnBackground {
            successWithBody.apply {
                if (!shouldSavedListBeUsed) {
                    castTo<Profile>(successWithBody.data)?.let {
                        addContentToProfileInfoList(it.profile)
                    }
                    localRepository.insertProfilesIntoDb(profilesInfoList)
                } else {
                    addContentToProfileInfoList(localRepository.getProfilesFromDb())
                }
                saveDataAfterSuccess(this)
            }
        }
    }

    fun obtainValueFromDataStore(): ProfilePreferences {
        runTaskOnForeground {
            profilePreferences =
                castTo(localRepository.obtainProtoDataStore().obtainData()) ?: ProfilePreferences.getDefaultInstance()
        }
        return profilePreferences
    }

    fun saveValueToDataStore(profile: ProfilePreferences) {
        runTaskOnForeground {
            localRepository.obtainProtoDataStore().updateData(
                castTo<ProfilePreferences>(profile)
            )
        }
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
        saveValueToDataStore(
            obtainValueFromDataStore().copy(currentProfile = obtainValueFromDataStore().temporaryCurrentProfile)
        )
        saveValueToDataStore(
            obtainValueFromDataStore().copy(hasLastCallBeenUnsuccessful = false)
        )
        saveValueToDataStore(
            obtainValueFromDataStore().copy(isThereAnOngoingCall = false)
        )
        if (obtainValueFromDataStore().hasUserDeletedProfileText &&
            obtainValueFromDataStore().profile.isNotEmpty()
        ) {
            saveValueToDataStore(
                obtainValueFromDataStore().copy(shouldASearchBePerformed = true)
            )
        } else {
            saveValueToDataStore(
                obtainValueFromDataStore().copy(shouldASearchBePerformed = false)
            )
        }

        obtainValueFromDataStore().apply {
            saveValueToDataStore(
                obtainValueFromDataStore().copy(
                    pageNumber = pageNumber.plus(
                        initialPage
                    )
                )
            )
        }

        _successStateFlow.emit(InitialSuccess)
        _successStateFlow.emit(
            SuccessWithBody(
                data = profilesInfoList,
                successWithBody.totalPages
            )
        )
    }

    fun checkDataAtStartup() {
        runTaskOnBackground {
            if (successStateFlow.value == InitialSuccess &&
                localRepository.getProfilesFromDb().isEmpty()
            ) {
                postIntermediateState(ActionNotRequired)
            } else if (localRepository.getProfilesFromDb().isNotEmpty() &&
                profilesInfoList.isEmpty()
            ) {
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(isLocalPopulation = true)
                )
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(hasASuccessfulCallAlreadyBeenMade = false)
                )
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

    internal inline fun <reified T> castTo(value: Any) = ValueCasting.castTo<T>(value)

    companion object {
        const val itemsPerPage = 20
        private const val initialPage = 1
    }
}
