package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.currentProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.githubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItems
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.pageNumber
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.temporaryCurrentProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.liveEvent.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault

class GithubProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: GenericGithubProfileRepository
) : ViewModel() {

    //Success LiveDatas
    private val _successLiveData = MutableLiveData<List<GithubProfileInformation>>()
    internal val successLiveData: LiveData<List<GithubProfileInformation>>
        get() = _successLiveData

    //Error LiveDatas
    private val _errorSingleLiveDataEvent = SingleLiveEvent<Int>()
    internal val errorSingleLiveDataEvent: LiveData<Int>
        get() = _errorSingleLiveDataEvent.toImmutableSingleLiveEvent()

    //Result lists
    private val _githubProfilesInfoList = mutableListOf<GithubProfileInformation>()
    private var githubProfilesInfoList = listOf<GithubProfileInformation>()

    @UnstableDefault
    internal fun requestUpdatedGithubProfiles(profile: String = emptyString) {

        saveStateValue(pageNumber, 1)

        if (profile.isNotEmpty()) {
            saveStateValue(temporaryCurrentProfile, profile)
            requestGithubProfiles(profile, true)
        } else {
            requestGithubProfiles(provideStateValue(temporaryCurrentProfile), true)
        }
    }

    @UnstableDefault
    internal fun requestMoreGithubProfiles() {
        requestGithubProfiles(provideStateValue(currentProfile), false)
    }

    @UnstableDefault
    private fun requestGithubProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean
    ) {
        viewModelScope.launch {
            if (shouldListItemsBeRemoved)
                handleCallResult(profile, shouldListItemsBeRemoved)
            else
                handleCallResult(provideStateValue(currentProfile), shouldListItemsBeRemoved)
        }
    }

    //This method handles both Success an Error states and delivers the result through a LiveData post to the view. Which in this case is GithubProfileInfoObtainmentActivity
    @UnstableDefault
    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {

        when (val value =
            repository.provideGithubUserInformation(
                user,
                provideStateValue(pageNumber),
                numberOfItemsPerPage
            )) {

            //Success state handling
            is APICallResult.Success<*> -> {

                saveStateValue(currentProfile, provideStateValue<String>(temporaryCurrentProfile))

                with(value.data as GithubProfilesList) {

                    saveStateValue(numberOfItems, githubProfileInformationList.size)

                    if (!provideStateValue<Boolean>(Constants.hasASuccessfulCallAlreadyBeenMade))
                        saveStateValue(Constants.hasASuccessfulCallAlreadyBeenMade, true)

                    if (shouldListItemsBeRemoved)
                        setupUpdatedList(githubProfileInformationList)
                    else
                        setupPaginationList(githubProfileInformationList)

                    saveStateValue(pageNumber, provideStateValue<Int>(pageNumber).plus(1))
                }
            }

            else -> handleErrorResult(value as APICallResult.Error)
        }
    }

    private fun handleErrorResult(errorValue: APICallResult.Error) {

        with(_errorSingleLiveDataEvent) {

            when (errorValue.error) {

                unknownHostException, socketTimeoutException, connectException -> errorProvider(
                    R.string.unknown_host_exception_and_socket_timeout_exception,
                    this
                )

                sslHandshakeException -> errorProvider(
                    R.string.ssl_handshake_exception,
                    this
                )

                clientSideError -> errorProvider(
                    R.string.client_side_error,
                    this
                )

                serverSideError -> errorProvider(
                    R.string.server_side_error,
                    this
                )

                forbidden -> errorProvider(
                    R.string.api_query_limit_exceeded_error,
                    this
                )

                else -> errorProvider(
                    R.string.generic_exception_and_generic_error,
                    this
                )
            }
        }
    }

    /* This method sets up the list with all default RecyclerViewItems it will need. In the first call, a Header is added at the top of the list and following it,
    the ProfileInformation item is added which is responsible for showing the searched user related data */
    private fun setupUpdatedList(
        githubProfileInformationList: List<GithubProfileInformation>
    ) {
        _githubProfilesInfoList.clear()
        _githubProfilesInfoList.addAll(githubProfileInformationList)
        githubProfilesInfoList = _githubProfilesInfoList
        saveStateValue(githubProfilesList, githubProfilesInfoList)
        _successLiveData.postValue(githubProfilesInfoList)
    }

    private fun setupPaginationList(githubProfileInformationList: List<GithubProfileInformation>) {
        _githubProfilesInfoList.addAll(githubProfileInformationList)
        githubProfilesInfoList = _githubProfilesInfoList
        saveStateValue(githubProfilesList, githubProfilesInfoList)
        _successLiveData.postValue(githubProfilesInfoList)
    }

    private fun errorProvider(
        error: Int,
        state: SingleLiveEvent<Int>
    ) {
        state.postValue(error)
    }

    //This method provides a URL to the profile a user clicks on a List item
    internal fun provideProfileUrlThroughViewModel(index: Int) =
        _githubProfilesInfoList[index].profileUrl

    internal inline fun <reified T : Any> provideStateValue(handleStateKey: String) =
        requireNotNull(savedStateHandle.get<T>(handleStateKey))

    internal fun <T> saveStateValue(handleStateKey: String, value: T) {
        savedStateHandle.set(handleStateKey, value)
    }
}