package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.*
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

    private var profilePreferences = ProfilePreferences.getDefaultInstance()

    private val _successStateFlow = MutableStateFlow<State<Success>>(InitialSuccess)
    internal val successStateFlow: StateFlow<State<Success>>
        get() = _successStateFlow

    private val _intermediateSharedFlow = MutableSharedFlow<State<Intermediate>>()
    internal val intermediateSharedFlow = _intermediateSharedFlow.asSharedFlow()

    private val _errorSharedFlow = MutableSharedFlow<State<Error>>()
    internal val errorSharedFlow = _errorSharedFlow.asSharedFlow()

    private val _profileInfoList = mutableListOf<UserProfile>()
    private var profilesInfoList: List<UserProfile> = _profileInfoList

    private var currentIntermediateState: State<Intermediate> = InitialIntermediate

    fun requestUpdatedProfiles(profile: String = emptyString) {
        saveValueToDataStore(
            obtainValueFromDataStore().toBuilder().setPageNumber(initialPageNumber).build()
        )

        if (profile.isNotEmpty()) {
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder().setTemporaryCurrentProfile(profile).build()
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
        if (shouldListItemsBeRemoved) {
            handleCall(profile, shouldListItemsBeRemoved)
        } else {
            handleCall(
                profile,
                shouldListItemsBeRemoved
            )
        }
    }

    private fun handleCall(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {
        runTaskOnBackground {
            val value =
                remoteRepository.provideGithubUserInformation(
                    user,
                    obtainValueFromDataStore().pageNumber,
                    numberOfItemsPerPage
                )
            handleResult(value, shouldListItemsBeRemoved)
        }
    }

    private suspend fun handleResult(value: Any, shouldListItemsBeRemoved: Boolean) {
        when (value) {
            is SuccessWithBody<*> -> {
                saveValueToDataStore(
                    obtainValueFromDataStore().toBuilder().setCurrentProfile(emptyString).build()
                )

                if (!obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                ) {
                    saveValueToDataStore(
                        obtainValueFromDataStore().toBuilder()
                            .setHasASuccessfulCallAlreadyBeenMade(true).build()
                    )
                }

                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(value)
                } else {
                    setupPaginationList(successWithBody = value)
                }

                saveValueToDataStore(
                    obtainValueFromDataStore().toBuilder().setNumberOfItems(profilesInfoList.size)
                        .build()
                )

                obtainValueFromDataStore().apply {
                    saveValueToDataStore(
                        toBuilder().setPageNumber(
                            pageNumber.plus(
                                initialPageNumber
                            )
                        ).build()
                    )
                }
            }

            SuccessWithoutBody -> Unit

            else -> {
                handleError(castTo(value))
            }
        }
    }

    private suspend fun handleError(error: State<Error>?) {
        error?.let {
            _errorSharedFlow.emit(error)
        }
    }

    private fun setupUpdatedList(
        successWithBody: SuccessWithBody<*>
    ) {
        runTaskOnBackground {
            successWithBody.apply {
                localRepository.dropProfileInformationTable(localRepository.getProfilesFromDb())
                _profileInfoList.clear()
                castTo<Profile>(successWithBody.data)?.let { profile ->
                    addContentToProfileInfoList(profile.profile)
                    localRepository.insertProfilesIntoDb(
                        profile.profile
                    )
                }
                postSuccessDataSaving(this)
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
                postSuccessDataSaving(this)
            }
        }
    }

    fun obtainValueFromDataStore(): ProfilePreferences {
        runTaskOnForeground {
            profilePreferences = castTo(localRepository.obtainProtoDataStore().obtainData())
        }
        return profilePreferences
    }

    fun saveValueToDataStore(profile: ProfilePreferences = ProfilePreferences.getDefaultInstance()) {
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

    private suspend fun postSuccessDataSaving(successWithBody: SuccessWithBody<*>) {
        saveValueToDataStore(
            obtainValueFromDataStore().toBuilder()
                .setHasLastCallBeenUnsuccessful(false).build()
        )
        saveValueToDataStore(
            obtainValueFromDataStore().toBuilder()
                .setIsThereAnOngoingCall(false)
                .build()
        )
        if (obtainValueFromDataStore().hasUserDeletedProfileText &&
            obtainValueFromDataStore().profile.isNotEmpty()
        ) {
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(true).build()
            )
        } else {
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder()
                    .setShouldASearchBePerformed(false).build()
            )
        }

        if (successWithBody.totalPages == SuccessWithBody.initialPosition) {
            _successStateFlow.emit(SuccessWithBody(data = Profile(profile = profilesInfoList)))
        } else {
            _successStateFlow.emit(successWithBody)
        }
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
                postIntermediateState(LocalPopulation)
            } else {
                postIntermediateState(StateRestoration)
            }
        }
    }

    private suspend fun postIntermediateState(state: State<Intermediate>) {
        if (state != currentIntermediateState) {
            currentIntermediateState = state
            _intermediateSharedFlow.emit(state)
        }
    }

    companion object {
        const val emptyString = ""
        const val numberOfItemsPerPage = 20
        private const val initialPageNumber = 1
    }
}
