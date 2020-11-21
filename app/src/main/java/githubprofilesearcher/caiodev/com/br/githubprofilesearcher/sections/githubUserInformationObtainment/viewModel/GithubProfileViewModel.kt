package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model.UserPreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.liveEvent.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult

class GithubProfileViewModel(
    private val localRepository: GenericLocalRepository,
    private val remoteRepository: GenericGithubProfileRepository
) : ViewModel() {

    private val _successLiveData = MutableLiveData<List<GithubProfileInformation>>()
    internal val successLiveData: LiveData<List<GithubProfileInformation>>
        get() = _successLiveData

    private val _errorSingleLiveDataEvent = SingleLiveEvent<Int>()
    internal val errorSingleLiveDataEvent: LiveData<Int>
        get() = _errorSingleLiveDataEvent.toImmutableSingleLiveEvent()

    private val _githubProfilesInfoList = mutableListOf<GithubProfileInformation>()
    private var githubProfilesInfoList: List<GithubProfileInformation> = _githubProfilesInfoList

    internal fun requestUpdatedGithubProfiles(profile: String = emptyString) {
        runTaskOnBackground {
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
    }

    internal fun requestMoreGithubProfiles() {
        runTaskOnBackground {
            requestGithubProfiles(obtainValueFromDataStore().currentProfile, false)
        }
    }

    private suspend fun requestGithubProfiles(
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

    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {
        val value =
            remoteRepository.provideGithubUserInformation(
                user,
                obtainValueFromDataStore().pageNumber,
                numberOfItemsPerPage
            )

        if (value is APICallResult.Success<*>) {
            saveValueToDataStore(obtainValueFromDataStore().copy(currentProfile = emptyString))
            with(castValue<GithubProfilesList>(value.data)) {
                if (!obtainValueFromDataStore().hasASuccessfulCallAlreadyBeenMade
                ) {
                    saveValueToDataStore(
                        obtainValueFromDataStore().copy(
                            hasASuccessfulCallAlreadyBeenMade = true
                        )
                    )
                }

                if (shouldListItemsBeRemoved) {
                    setupUpdatedList(githubProfileInformationList)
                } else {
                    setupPaginationList(githubProfileInformationList = githubProfileInformationList)
                }

                saveValueToDataStore(obtainValueFromDataStore().copy(numberOfItems = githubProfilesInfoList.size))
                saveValueToDataStore(
                    obtainValueFromDataStore().copy(
                        pageNumber = obtainValueFromDataStore().pageNumber.plus(
                            1
                        )
                    )
                )
            }
        } else {
            handleErrorResult(castValue(value))
        }
    }

    private fun handleErrorResult(errorValue: APICallResult.Error) {
        with(_errorSingleLiveDataEvent) {
            when (errorValue.error) {
                unknownHostException, socketTimeoutException, connectException -> providerError(
                    R.string.unknown_host_exception_and_socket_timeout_exception,
                    this
                )

                sslHandshakeException -> providerError(
                    R.string.ssl_handshake_exception,
                    this
                )

                clientSideError -> providerError(
                    R.string.client_side_error,
                    this
                )

                serverSideError -> providerError(
                    R.string.server_side_error,
                    this
                )

                forbidden -> providerError(
                    R.string.api_query_limit_exceeded_error,
                    this
                )

                else -> providerError(
                    R.string.generic_exception_and_generic_error,
                    this
                )
            }
        }
    }

    private fun setupUpdatedList(
        githubProfileInformationList: List<GithubProfileInformation>
    ) {
        _githubProfilesInfoList.clear()
        addContentToGithubProfilesInfoList(githubProfileInformationList)
        runTaskOnBackground {
            localRepository.insertGithubProfilesIntoDb(
                githubProfileInformationList
            )
        }
        _successLiveData.postValue(githubProfilesInfoList)
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

    private fun providerError(
        error: Int,
        state: SingleLiveEvent<Int>
    ) {
        state.postValue(error)
    }

    internal suspend fun obtainValueFromDataStore() =
        localRepository.obtainProtoDataStore().obtainDataStoreValue()

    internal suspend fun saveValueToDataStore(
        userPreferences: UserPreferences = UserPreferences()
    ) {
        localRepository.obtainProtoDataStore().updateDataStoreValue(userPreferences)
    }

    private fun addContentToGithubProfilesInfoList(list: List<GithubProfileInformation>) {
        _githubProfilesInfoList.addAll(list)
    }

    internal fun updateUIWithCache() {
        setupPaginationList(shouldSavedListBeUsed = true)
    }
}
