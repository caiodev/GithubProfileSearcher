package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.connectException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toImmutableSingleLiveEvent
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

    //Transient list item view flags
    private var isEndOfResultsItemVisible = false
    private var isPaginationLoadingItemVisible = false
    internal var isRetryItemVisible = false

    //Success LiveDatas
    private val successMutableLiveData = MutableLiveData<List<GithubProfileInformation>>()
    internal val successLiveData: LiveData<List<GithubProfileInformation>>
        get() = successMutableLiveData

    //Error LiveDatas
    private val errorSingleMutableLiveDataEvent =
        SingleLiveEvent<Int>()
    internal val errorSingleImmutableLiveDataEvent: LiveData<Int>
        get() = errorSingleMutableLiveDataEvent.toImmutableSingleLiveEvent()

    //Result lists
    private val githubProfilesInfoMutableList = mutableListOf<GithubProfileInformation>()
    var githubProfilesInfoList: List<GithubProfileInformation> =
        githubProfilesInfoMutableList

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
            if (shouldListItemsBeRemoved)
                handleCallResult(profile, shouldListItemsBeRemoved)
            else
                handleCallResult(currentProfile, shouldListItemsBeRemoved)
        }
    }

    //This method handles both Success an Error states and delivers the result through a LiveData post to the view. Which in this case is GithubProfileInfoObtainmentActivity
    @UnstableDefault
    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {

//        if (!hasUserTriggeredANewRequest)

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

                    if (shouldListItemsBeRemoved)
                        setupList(githubProfileInformationList)
                    else {

//                        if (hasLastCallBeenSuccessful() && isPaginationLoadingItemVisible)

                        githubProfilesInfoMutableList.addAll(githubProfileInformationList)


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

//        if (!isRetryItemVisible)

        with(errorSingleMutableLiveDataEvent) {

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
    private fun setupList(
        githubUserInformationList: List<GithubProfileInformation>
    ) {
        githubProfilesInfoMutableList.clear()
//        isPaginationLoadingItemVisible = false
//        isRetryItemVisible = false
//        isEndOfResultsItemVisible = false

        githubUserInformationList.forEach {
            populateList(it)
        }

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

    private fun errorProvider(
        error: Int,
        state: SingleLiveEvent<Int>
    ) {
        state.postValue(error)
    }

//    private fun insertTransientItemIntoTheResultsList(
//        state: Int,
//        shouldPostValue: Boolean = false
//    ) {
//
//        if (hasLastCallBeenSuccessful()) {
//
//            when (state) {
//
//                loading -> {
//                    isThereAnOngoingCall = true
//                    if (isRetryItemVisible) dropLast()
//                    isPaginationLoadingItemVisible = true
//                    isRetryItemVisible = false
//                    isEndOfResultsItemVisible = false
//                }
//
//                retry -> {
//                    if (isPaginationLoadingItemVisible) dropLast()
//                    isRetryItemVisible = true
//                    isPaginationLoadingItemVisible = false
//                    isEndOfResultsItemVisible = false
//                }
//
//                else -> {
//                    if (isPaginationLoadingItemVisible || isRetryItemVisible) dropLast()
//                    isEndOfResultsItemVisible = true
//                    isPaginationLoadingItemVisible = false
//                    isRetryItemVisible = false
//                }
//            }
//        }
//
//        githubProfilesInfoList = githubProfilesInfoMutableList
//
//        if (shouldPostValue) successMutableLiveData.postValue(githubProfilesInfoList)
//    }

    private fun hasLastCallBeenSuccessful() = githubProfilesInfoList.isNotEmpty()

    //This method provides a URL to the profile a user clicks on a List item
    internal fun provideProfileUrlThroughViewModel(index: Int) =
        githubProfilesInfoMutableList[index].profileUrl
}