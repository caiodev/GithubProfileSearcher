package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.LiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.genericError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class GithubProfileInfoObtainmentViewModel(
    private val githubUserInformationRepository:
    GithubProfileInformationRepository
) : ViewModel() {

    //Success LiveDatas
    private val successMutableLiveData = MutableLiveData<List<ViewType>>()
    internal val successLiveData: LiveData<List<ViewType>> = successMutableLiveData

    //Error LiveDatas
    private val errorSingleMutableLiveDataEvent = LiveEvent<Pair<Int, Int>>()
    internal val errorSingleImmutableLiveDataEvent =
        errorSingleMutableLiveDataEvent.toImmutableSingleEvent()

    //Result lists
    private val githubProfilesInfoMutableList = mutableListOf<ViewType>()
    private var githubProfilesInfoList = listOf<ViewType>()

    private val errorStatePair = Pair(0, 0)

    //Flags
    private var pageNumber = 1

    //Call related flags
    internal var hasFirstSuccessfulCallBeenMade = false
    internal var hasLastCallBeenSuccessful = false
    internal var haveUsersHadAnyTroubleDuringTheFirstCall = false
    internal var isThereAnOngoingCall = false

    internal var hasAnyUserRequestedUpdatedData = false
    internal var shouldASearchBePerformed = false

    private var currentProfile = ""
    internal var isThereAnyProfileToBeSearched = false
    internal var lastVisibleListItem = 0
    internal var isTheNumberOfItemsOfTheLastCallLessThanTwenty = false

    //These variables keep track of all calls made successful or not
    internal var successfulCallsCount = 0
    internal var unsuccessfulCallsCount = 0

    fun getGithubProfileList(
        profile: String? = null,
        shouldListItemsBeRemoved: Boolean? = null
    ) {

        profile?.let {
            currentProfile = it
        }

        shouldListItemsBeRemoved?.let {
            if (hasAnyUserRequestedUpdatedData || it) pageNumber = 1
            pageNumber
        }

        viewModelScope.launch {
            handleCallResult(currentProfile, shouldListItemsBeRemoved)
        }
    }

    private fun populateList(githubInfo: GithubProfileInformation) {
        githubProfilesInfoMutableList.add(
            GithubProfileInformation(
                githubInfo.userId,
                githubInfo.login,
                githubInfo.userImage,
                githubInfo.profileUrl,
                githubInfo.name,
                githubInfo.score,
                githubInfo.bio,
                githubInfo.numberOfFollowers,
                githubInfo.numberOfRepositories
            )
        )
    }

    fun provideProfileUrlThroughViewModel(adapterPosition: Int) =
        (githubProfilesInfoMutableList[adapterPosition] as GithubProfileInformation).profileUrl

    private fun setupList(
        githubUserInformationList: MutableList<GithubProfileInformation>
    ) {
        if (githubProfilesInfoMutableList.isNotEmpty()) githubProfilesInfoMutableList.clear()
        githubProfilesInfoMutableList.add(Header(R.string.github_user_list_header))
        githubUserInformationList.forEach {
            populateList(it)
        }
        githubProfilesInfoList = githubProfilesInfoMutableList
        successMutableLiveData.postValue(githubProfilesInfoList)
    }

    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean? = null
    ) {
        when (val value =
            githubUserInformationRepository.provideGithubUserInformation(
                user,
                pageNumber,
                numberOfItemsPerPage
            )) {

            is APICallResult.Success<*> -> {
                successfulCallsCount++
                isThereAnOngoingCall = false
                hasLastCallBeenSuccessful = true
                with(value.data as GithubProfilesList) {
                    if (githubProfileInformationList.size < 20) isTheNumberOfItemsOfTheLastCallLessThanTwenty = true
                    shouldListItemsBeRemoved?.let {
                        if (it)
                            setupList(githubProfileInformationList)
                        else {
                            githubProfilesInfoMutableList.addAll(githubProfileInformationList)
                            successMutableLiveData.postValue(githubProfilesInfoMutableList)
                        }
                    }
                    pageNumber++
                }
            }

            is APICallResult.Error -> {
                unsuccessfulCallsCount++
                isThereAnOngoingCall = false
                hasAnyUserRequestedUpdatedData = false
                hasLastCallBeenSuccessful = false

                with(errorSingleMutableLiveDataEvent) {
                    when (value.error) {
                        unknownHostException, socketTimeoutException, connectException -> errorPairProvider(
                            unknownHostException,
                            R.string.unknown_host_exception_and_socket_timeout_exception,
                            this
                        )
                        sslHandshakeException -> errorPairProvider(
                            sslHandshakeException,
                            R.string.ssl_handshake_exception,
                            this
                        )
                        clientSideError -> errorPairProvider(
                            clientSideError,
                            R.string.client_side_error,
                            this
                        )
                        serverSideError -> errorPairProvider(
                            serverSideError,
                            R.string.server_side_error,
                            this
                        )
                        forbidden -> errorPairProvider(
                            forbidden,
                            R.string.api_query_limit_exceeded_error,
                            this
                        )
                        else -> errorPairProvider(
                            genericError,
                            R.string.generic_exception_and_generic_error,
                            this
                        )
                    }
                }
            }
        }
    }

    private fun errorPairProvider(
        errorState: Int,
        errorString: Int,
        state: LiveEvent<Pair<Int, Int>>
    ) {
        state.postValue(errorStatePair.copy(errorState, errorString))
    }
}