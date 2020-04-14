package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.*
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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.dropLast
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.liveEvent.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault

class GithubProfileViewModel(
    private val repository:
    GenericGithubProfileRepository
) : ViewModel() {

    //Information cache variables
    private var temporaryCurrentProfile = ""
    private var currentProfile = ""
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
    private var isEndOfResultsItemVisible = false
    private var isPaginationLoadingItemVisible = false
    internal var isRetryItemVisible = false

    //Success LiveDatas
    private val successMutableLiveData = MutableLiveData<List<ViewType>>()
    internal val successLiveData: LiveData<List<ViewType>>
        get() = successMutableLiveData

    //Error LiveDatas
    private val errorSingleMutableLiveDataEvent =
        SingleLiveEvent<Int>()
    internal val errorSingleImmutableLiveDataEvent: LiveData<Int>
        get() = errorSingleMutableLiveDataEvent.toImmutableSingleLiveEvent()

    //Result lists
    private val githubProfilesInfoMutableList = mutableListOf<ViewType>()
    private var githubProfilesInfoList: List<ViewType> = githubProfilesInfoMutableList

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
    private fun requestGithubProfiles(
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
                        if (hasLastCallBeenSuccessful() && isPaginationLoadingItemVisible) {
                            dropLast()
                        }
                        githubProfilesInfoMutableList.addAll(githubProfileInformationList)
                        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
                            endOfResults, true
                        )
                        githubProfilesInfoList = githubProfilesInfoMutableList
                        successMutableLiveData.postValue(githubProfilesInfoList)
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
        isPaginationLoadingItemVisible = false
        isRetryItemVisible = false
        isEndOfResultsItemVisible = false

        githubProfilesInfoMutableList.add(Header(R.string.github_user_list_header))

        githubUserInformationList.forEach {
            populateList(it)
        }

        if (isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
            endOfResults
        )
        githubProfilesInfoList = githubProfilesInfoMutableList
        successMutableLiveData.postValue(githubProfilesInfoList)
    }

    //This method populates the GithubUserProfile related information list which in this case is githubProfilesInfoMutableList
    private fun populateList(githubInfo: GithubProfileInformation) {
        githubProfilesInfoMutableList.add(
            GithubProfileInformation(
                githubInfo.login,
                githubInfo.profileUrl,
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
                    if (isRetryItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(Loading())
                    isPaginationLoadingItemVisible = true
                    isRetryItemVisible = false
                    isEndOfResultsItemVisible = false
                }

                retry -> {
                    if (isPaginationLoadingItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(Retry())
                    isRetryItemVisible = true
                    isPaginationLoadingItemVisible = false
                    isEndOfResultsItemVisible = false
                }

                else -> {
                    if (isPaginationLoadingItemVisible || isRetryItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(EndOfResults())
                    isEndOfResultsItemVisible = true
                    isPaginationLoadingItemVisible = false
                    isRetryItemVisible = false
                }
            }
        }

        githubProfilesInfoList = githubProfilesInfoMutableList

        if (shouldPostValue) successMutableLiveData.postValue(githubProfilesInfoList)
    }

    private fun dropLast() {
        dropLast(githubProfilesInfoMutableList)
    }

    private fun hasLastCallBeenSuccessful() = githubProfilesInfoList.isNotEmpty()

    //This method provides a URL to the profile a user clicks on a List item
    internal fun provideProfileUrlThroughViewModel(index: Int) =
        (githubProfilesInfoMutableList[index] as GithubProfileInformation).profileUrl
}