package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.Repository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.liveEvent.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault

class GithubProfileInfoObtainmentViewModel(
    private val repository:
    Repository
) : ViewModel() {

    //Success LiveDatas
    private val mainListMutableLiveData = MutableLiveData<List<ViewType>>()
    internal val mainListLiveData: LiveData<List<ViewType>>
        get() = mainListMutableLiveData

    //Error LiveDatas
    private val errorSingleMutableLiveDataEvent =
        SingleLiveEvent<Int>()
    internal val errorSingleImmutableLiveDataEvent: LiveData<Int>
        get() = errorSingleMutableLiveDataEvent.toImmutableSingleLiveEvent()

    //Result lists
    private val githubProfilesInfoMutableList = mutableListOf<ViewType>()
    private var githubProfilesInfoList: List<ViewType> = githubProfilesInfoMutableList

    //Information cache variables
    private var temporaryCurrentProfile = ""
    private var currentProfile = ""
    internal var lastVisibleListItem = 0

    //Flags
    private var pageNumber = 1

    //Call related flags
    internal var hasFirstSuccessfulCallBeenMade = false
    internal var isThereAnOngoingCall = false
    internal var hasUserTriggeredANewRequest = false

    internal var hasAnyUserRequestedUpdatedData = false
    internal var shouldASearchBePerformed = true

    internal var isThereAnyProfileToBeSearched = false
    internal var isTheNumberOfItemsOfTheLastCallLessThanTwenty = false

    //Transient list item view flags
    private var isEndOfResultsListItemVisible = false
    private var isPaginationLoadingListItemVisible = false
    internal var isRetryListItemVisible = false

    @UnstableDefault
    internal fun requestUpdatedGithubProfiles(profile: String = temporaryCurrentProfile) {
        hasAnyUserRequestedUpdatedData = true
        temporaryCurrentProfile = profile
        requestGithubProfiles(profile, true)
    }

    @UnstableDefault
    internal fun requestMoreGithubProfiles() {
        requestGithubProfiles(currentProfile, false)
    }

    //This method is where all the network request process starts. First, when it is called,
    @UnstableDefault
    internal fun requestGithubProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean
    ) {

        isThereAnOngoingCall = true

        if (hasAnyUserRequestedUpdatedData || shouldListItemsBeRemoved || hasUserTriggeredANewRequest) pageNumber =
            1

        viewModelScope.launch {
            if (shouldListItemsBeRemoved) {
                handleCallResult(profile, shouldListItemsBeRemoved)
            } else
                handleCallResult(currentProfile, shouldListItemsBeRemoved)
        }
    }

    //This method provides a URL to the profile the user clicked on
    internal fun provideProfileUrlThroughViewModel(index: Int) =
        (githubProfilesInfoMutableList[index] as GithubProfileInformation).profileUrl

    //This method handles both Success an Error states and delivers the result through a LiveData post to the view. Which in this case is GithubProfileInfoObtainmentActivity
    @UnstableDefault
    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {

        if (!hasUserTriggeredANewRequest) insertTransientItemIntoTheResultsList(loading, true)

        when (val value =
            repository.provideGithubUserInformation(
                user,
                pageNumber,
                numberOfItemsPerPage
            )) {

            //Success state handling
            is APICallResult.Success<*> -> {

                if (!hasLastCallBeenSuccessful()) hasFirstSuccessfulCallBeenMade = true
                currentProfile = temporaryCurrentProfile
                isThereAnOngoingCall = false
                with(value.data as GithubProfilesList) {

                    isTheNumberOfItemsOfTheLastCallLessThanTwenty =
                        githubProfileInformationList.size < 20

                    if (shouldListItemsBeRemoved)
                        setupList(githubProfileInformationList)
                    else {
                        if (hasLastCallBeenSuccessful() && isPaginationLoadingListItemVisible) removeLastItem()
                        githubProfilesInfoMutableList.addAll(githubProfileInformationList)
                        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
                            endOfResults, true
                        )
                        githubProfilesInfoList = githubProfilesInfoMutableList
                        mainListMutableLiveData.postValue(githubProfilesInfoList)
                    }
                    pageNumber++
                }
            }

            else -> handleErrorResult(value as APICallResult.Error)
        }
    }

    private fun handleErrorResult(errorValue: APICallResult.Error) {

        //Error state handling
        isThereAnOngoingCall = false
        hasAnyUserRequestedUpdatedData = false

        insertTransientItemIntoTheResultsList(retry, true)

        with(errorSingleMutableLiveDataEvent) {

            when (errorValue.error) {

                unknownHostException, socketTimeoutException, connectException -> errorPairProvider(
                    R.string.unknown_host_exception_and_socket_timeout_exception,
                    this
                )

                sslHandshakeException -> errorPairProvider(
                    R.string.ssl_handshake_exception,
                    this
                )

                clientSideError -> errorPairProvider(
                    R.string.client_side_error,
                    this
                )

                serverSideError -> errorPairProvider(
                    R.string.server_side_error,
                    this
                )

                forbidden -> errorPairProvider(
                    R.string.api_query_limit_exceeded_error,
                    this
                )

                else -> errorPairProvider(
                    R.string.generic_exception_and_generic_error,
                    this
                )
            }
        }

    }

    /* This method sets up the list with all default RecyclerViewItems it will need. In the first call, a Header is added at the top of the list and following it,
    the ProfileInformation item is added which is responsible for showing the searched user related data */
    private fun setupList(
        githubUserInformationList: List<GithubProfileInformation>
    ) {
        githubProfilesInfoMutableList.clear()
        isPaginationLoadingListItemVisible = false
        isRetryListItemVisible = false
        isEndOfResultsListItemVisible = false
        githubProfilesInfoMutableList.add(Header(R.string.github_user_list_header))
        githubUserInformationList.forEach {
            populateList(it)
        }

        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
            endOfResults
        )
        githubProfilesInfoList = githubProfilesInfoMutableList
        mainListMutableLiveData.postValue(githubProfilesInfoList)
    }

    //This method populates the GithubUserProfile related information list which in this case is githubProfilesInfoMutableList
    private fun populateList(githubInfo: GithubProfileInformation) {
        githubProfilesInfoMutableList.add(
            GithubProfileInformation(
                githubInfo.login,
                githubInfo.profileUrl,
                githubInfo.score,
                githubInfo.userId,
                githubInfo.userImage
            )
        )
    }

    private fun errorPairProvider(
        error: Int,
        state: SingleLiveEvent<Int>
    ) {
        state.postValue(error)
    }

    private fun insertTransientItemIntoTheResultsList(
        state: Int,
        shouldPostValue: Boolean = false
    ) {

        if (hasLastCallBeenSuccessful()) {

            when (state) {

                loading -> {
                    isThereAnOngoingCall = true
                    if (isRetryListItemVisible) removeLastItem()
                    githubProfilesInfoMutableList.add(Loading())
                    isPaginationLoadingListItemVisible = true
                    isRetryListItemVisible = false
                    isEndOfResultsListItemVisible = false
                }

                retry -> {
                    if (isPaginationLoadingListItemVisible) removeLastItem()
                    githubProfilesInfoMutableList.add(Retry())
                    isRetryListItemVisible = true
                    isPaginationLoadingListItemVisible = false
                    isEndOfResultsListItemVisible = false
                }

                else -> {
                    if (isPaginationLoadingListItemVisible || isRetryListItemVisible) removeLastItem()
                    githubProfilesInfoMutableList.add(EndOfResults())
                    isEndOfResultsListItemVisible = true
                    isPaginationLoadingListItemVisible = false
                    isRetryListItemVisible = false
                }
            }
        }

        githubProfilesInfoList = githubProfilesInfoMutableList

        if (shouldPostValue) mainListMutableLiveData.postValue(githubProfilesInfoList)
    }

    private fun removeLastItem() {
        githubProfilesInfoMutableList.removeAt(githubProfilesInfoMutableList.size - 1)
    }

    private fun hasLastCallBeenSuccessful() = githubProfilesInfoList.isNotEmpty()
}