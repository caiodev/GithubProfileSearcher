package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Error
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnForeground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ProfileViewModel(
    private val networkChecking: NetworkChecking,
    private val localRepository: ILocalRepository,
    private val remoteRepository: IProfileRepository
) : ViewModel() {

    private val _successMutableStateFlow = MutableStateFlow<List<UserProfileInformation>>(listOf())
    val successStateFlow: StateFlow<List<UserProfileInformation>>
        get() = _successMutableStateFlow

    private val _errorStateFlow = MutableStateFlow<State<Error>>(Generic)
    val errorStateFlow: StateFlow<State<Error>>
        get() = _errorStateFlow

    private val _profileInfoList = mutableListOf<UserProfileInformation>()
    private var profilesInfoList: List<UserProfileInformation> = _profileInfoList

    fun requestUpdatedGithubProfiles(profile: String = emptyString) {
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

    fun requestMoreGithubProfiles() {
        requestProfiles(obtainValueFromDataStore().currentProfile, false)
    }

    private fun requestProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean
    ) {
        if (shouldListItemsBeRemoved) {
            handleCallResult(profile, shouldListItemsBeRemoved)
        } else {
            handleCallResult(
                profile,
                shouldListItemsBeRemoved
            )
        }
    }

    private fun handleCallResult(
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
            handleSuccess(value, shouldListItemsBeRemoved)
        }
    }

    private suspend fun handleSuccess(value: Any, shouldListItemsBeRemoved: Boolean) {
        if (value is Success<*>) {
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

            castTo<Profile>(value.data)?.apply {
                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(githubProfileInformationList)
                } else {
                    setupPaginationList(githubProfileInformationList = githubProfileInformationList)
                }
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
        } else {
            handleError(castTo(value))
        }
    }

    private suspend fun handleError(error: State<Error>?) {
        error?.let {
            _errorStateFlow.emit(error)
        }
    }

    private fun setupUpdatedList(
        githubProfileInformationList: List<UserProfileInformation>
    ) {
        runTaskOnBackground {
            localRepository.dropProfileInformationTable(localRepository.getProfilesFromDb())
            _profileInfoList.clear()
            addContentToGithubProfilesInfoList(githubProfileInformationList)
            localRepository.insertProfilesIntoDb(
                githubProfileInformationList
            )
            println("Emit: Success1")
            _successMutableStateFlow.emit(profilesInfoList)
        }
    }

    private fun setupPaginationList(
        shouldSavedListBeUsed: Boolean = false,
        githubProfileInformationList: List<UserProfileInformation> = listOf()
    ) {
        runTaskOnBackground {
            if (!shouldSavedListBeUsed) {
                addContentToGithubProfilesInfoList(githubProfileInformationList)
                localRepository.insertProfilesIntoDb(profilesInfoList)
            } else {
                addContentToGithubProfilesInfoList(localRepository.getProfilesFromDb())
            }
            if (profilesInfoList.isNotEmpty()) {
                println("Emit: Success2")
                _successMutableStateFlow.emit(profilesInfoList)
            }
        }
    }

    fun obtainValueFromDataStore(): ProfilePreferences {
        var profilePreferences: ProfilePreferences? = null
        runTaskOnForeground {
            profilePreferences = castTo(localRepository.obtainProtoDataStore().obtainData())
        }
        return profilePreferences ?: ProfilePreferences.getDefaultInstance()
    }

    fun saveValueToDataStore(profile: ProfilePreferences = ProfilePreferences.getDefaultInstance()) {
        runTaskOnForeground {
            localRepository.obtainProtoDataStore().updateData(
                castTo<ProfilePreferences>(profile)
            )
        }
    }

    private fun addContentToGithubProfilesInfoList(list: List<UserProfileInformation>) {
        _profileInfoList.addAll(list)
    }

    fun updateUIWithCache() {
        setupPaginationList(shouldSavedListBeUsed = true)
    }

    fun obtainConnectionState() = networkChecking.checkIfInternetConnectionIsAvailable()

    fun provideConnectionObserver() = networkChecking.observeInternetConnectionAvailability()

    fun isObserverUnchanged() = successStateFlow.value.isEmpty()

    fun isResultListEmpty() = profilesInfoList.isEmpty()

    companion object {
        const val emptyString = ""
        const val numberOfItemsPerPage = 20
        private const val initialPageNumber = 1
    }
}
