package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.States
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnForeground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class GithubProfileViewModel(
    private val localRepository: ILocalRepository,
    private val remoteRepository: IProfileRepository
) : ViewModel() {

    private val _successLiveData = MutableLiveData<List<UserProfileInformation>>()
    val successLiveData: LiveData<List<UserProfileInformation>>
        get() = _successLiveData

    private val _errorStateFlow = MutableStateFlow<States<States.Error>>(States.Generic)
    val errorStateFlow: StateFlow<States<States.Error>>
        get() = _errorStateFlow

    private val _githubProfilesInfoList = mutableListOf<UserProfileInformation>()
    private var githubProfilesInfoList: List<UserProfileInformation> = _githubProfilesInfoList

    fun requestUpdatedGithubProfiles(profile: String = emptyString) {
        saveValueToDataStore(
            obtainValueFromDataStore().toBuilder().setPageNumber(initialPageNumber).build()
        )

        if (profile.isNotEmpty()) {
            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder().setTemporaryCurrentProfile(profile).build()
            )
            requestGithubProfiles(profile, true)
        } else {
            requestGithubProfiles(
                obtainValueFromDataStore().temporaryCurrentProfile,
                true
            )
        }
    }

    fun requestMoreGithubProfiles() {
        requestGithubProfiles(obtainValueFromDataStore().currentProfile, false)
    }

    private fun requestGithubProfiles(
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
        if (value is States.Success<*>) {
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

            castValue<Profile>(value.data).apply {
                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(githubProfileInformationList)
                } else {
                    setupPaginationList(githubProfileInformationList = githubProfileInformationList)
                }
            }

            saveValueToDataStore(
                obtainValueFromDataStore().toBuilder().setNumberOfItems(githubProfilesInfoList.size)
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
            handleError(castValue(value))
        }
    }

    private suspend fun handleError(error: States<States.Error>) {
        _errorStateFlow.emit(error)
    }

    private fun setupUpdatedList(
        githubProfileInformationList: List<UserProfileInformation>
    ) {
        runTaskOnBackground {
            localRepository.dropProfileInformationTable(localRepository.getProfilesFromDb())
            _githubProfilesInfoList.clear()
            addContentToGithubProfilesInfoList(githubProfileInformationList)
            localRepository.insertProfilesIntoDb(
                githubProfileInformationList
            )
            _successLiveData.postValue(githubProfilesInfoList)
        }
    }

    private fun setupPaginationList(
        shouldSavedListBeUsed: Boolean = false,
        githubProfileInformationList: List<UserProfileInformation> = listOf()
    ) {
        runTaskOnBackground {
            if (!shouldSavedListBeUsed) {
                addContentToGithubProfilesInfoList(githubProfileInformationList)
                localRepository.insertProfilesIntoDb(githubProfilesInfoList)
            } else {
                addContentToGithubProfilesInfoList(localRepository.getProfilesFromDb())
            }
            _successLiveData.postValue(githubProfilesInfoList)
        }
    }

    fun obtainValueFromDataStore(): ProfilePreferences {
        var profilePreferences: ProfilePreferences? = null
        runTaskOnForeground {
            profilePreferences = castValue(localRepository.obtainProtoDataStore().obtainData())
        }
        return profilePreferences ?: ProfilePreferences.getDefaultInstance()
    }

    fun saveValueToDataStore(profilePreferences: ProfilePreferences = ProfilePreferences.getDefaultInstance()) {
        runTaskOnForeground {
            localRepository.obtainProtoDataStore().updateData(
                castValue<ProfilePreferences>(profilePreferences)
            )
        }
    }

    private fun addContentToGithubProfilesInfoList(list: List<UserProfileInformation>) {
        _githubProfilesInfoList.addAll(list)
    }

    fun updateUIWithCache() {
        setupPaginationList(shouldSavedListBeUsed = true)
    }

    companion object {
        const val emptyString = ""
        const val numberOfItemsPerPage = 20
        private const val initialPageNumber = 1
    }
}
