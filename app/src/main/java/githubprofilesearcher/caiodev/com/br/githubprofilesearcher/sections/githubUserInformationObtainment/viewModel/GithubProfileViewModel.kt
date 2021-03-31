package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ProfilePreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.States
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runDataStoreTask
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class GithubProfileViewModel(
    private val localRepository: GenericLocalRepository,
    private val remoteRepository: GenericGithubProfileRepository
) : ViewModel() {

    private val _successLiveData = MutableLiveData<List<GithubProfileInformation>>()
    val successLiveData: LiveData<List<GithubProfileInformation>>
        get() = _successLiveData

    private val _errorStateFlow = MutableStateFlow<States<States.Error>>(States.Generic)
    val errorStateFlow: StateFlow<States<States.Error>>
        get() = _errorStateFlow

    private val _githubProfilesInfoList = mutableListOf<GithubProfileInformation>()
    private var githubProfilesInfoList: List<GithubProfileInformation> = _githubProfilesInfoList

    fun requestUpdatedGithubProfiles(profile: String = emptyString) {
        saveValueToDataStore(obtainValueFromDataStore().copy(pageNumber = 1))

        if (profile.isNotEmpty()) {
            saveValueToDataStore(obtainValueFromDataStore().copy(temporaryCurrentProfile = profile))
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
            saveValueToDataStore(obtainValueFromDataStore().copy(currentProfile = emptyString))
            if (!obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
            ) {
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(
                        hasASuccessfulCallAlreadyBeenMade = true
                    )
                )
            }

            castValue<Profile>(value.data).apply {
                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(githubProfileInformationList)
                } else {
                    setupPaginationList(githubProfileInformationList = githubProfileInformationList)
                }
            }

            saveValueToDataStore(obtainValueFromDataStore().copy(numberOfItems = githubProfilesInfoList.size))
            saveValueToDataStore(
                obtainValueFromDataStore().copy(
                    pageNumber = obtainValueFromDataStore().pageNumber.plus(
                        1
                    )
                )
            )
        } else {
            handleError(castValue(value))
        }
    }

    private suspend fun handleError(error: States<States.Error>) {
        _errorStateFlow.emit(error)
    }

    private fun setupUpdatedList(
        githubProfileInformationList: List<GithubProfileInformation>
    ) {
        runTaskOnBackground {
            localRepository.dropGithubProfileInformationTable(localRepository.getGithubProfilesFromDb())
            _githubProfilesInfoList.clear()
            addContentToGithubProfilesInfoList(githubProfileInformationList)
            localRepository.insertGithubProfilesIntoDb(
                githubProfileInformationList
            )
            _successLiveData.postValue(githubProfilesInfoList)
        }
    }

    private fun setupPaginationList(
        shouldSavedListBeUsed: Boolean = false,
        githubProfileInformationList: List<GithubProfileInformation> = listOf()
    ) {
        runTaskOnBackground {
            if (!shouldSavedListBeUsed) {
                addContentToGithubProfilesInfoList(githubProfileInformationList)
                localRepository.insertGithubProfilesIntoDb(githubProfilesInfoList)
            } else {
                addContentToGithubProfilesInfoList(localRepository.getGithubProfilesFromDb())
            }
            _successLiveData.postValue(githubProfilesInfoList)
        }
    }

    fun obtainValueFromDataStore(): ProfilePreferences {
        var profilePreferences: ProfilePreferences? = null
        runDataStoreTask {
            profilePreferences =
                localRepository.obtainProtoDataStore().obtainValue<ProfilePreferences>()
        }
        return profilePreferences ?: ProfilePreferences.getDefaultInstance()
    }

    fun saveValueToDataStore(
        profilePreferences: ProfilePreferences = ProfilePreferences.getDefaultInstance()
    ) {
        runDataStoreTask {
            localRepository.obtainProtoDataStore().updateValue(profilePreferences)
            profilePreferences.toBuilder().setPageNumber(2).build()        }
    }

    private fun addContentToGithubProfilesInfoList(list: List<GithubProfileInformation>) {
        _githubProfilesInfoList.addAll(list)
    }

    fun updateUIWithCache() {
        setupPaginationList(shouldSavedListBeUsed = true)
    }

    companion object {
        const val emptyString = ""
        const val numberOfItemsPerPage = 20
    }
}
