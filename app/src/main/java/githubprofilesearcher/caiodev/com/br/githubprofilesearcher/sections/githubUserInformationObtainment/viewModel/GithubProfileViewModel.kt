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
    GenericGithubProfileRepository,
    val flags: GithubProfileViewModelFlags
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

    @UnstableDefault
    internal fun requestUpdatedGithubProfiles(profile: String = flags.temporaryCurrentProfile) {
        flags.hasAnyUserRequestedUpdatedData = true
        flags.temporaryCurrentProfile = profile
        requestGithubProfiles(profile, true)
    }

    @UnstableDefault
    internal fun requestMoreGithubProfiles() {
        requestGithubProfiles(flags.currentProfile, false)
    }

    //This method is where all the utils.utils.network request process starts. First, when it is called,
    @UnstableDefault
    internal fun requestGithubProfiles(
        profile: String,
        shouldListItemsBeRemoved: Boolean
    ) {

        flags.isThereAnOngoingCall = true

        if (flags.hasAnyUserRequestedUpdatedData || shouldListItemsBeRemoved || flags.hasUserTriggeredANewRequest) flags.pageNumber =
            1

        viewModelScope.launch {
            if (shouldListItemsBeRemoved) {
                handleCallResult(profile, shouldListItemsBeRemoved)
            } else
                handleCallResult(flags.currentProfile, shouldListItemsBeRemoved)
        }
    }

    //This method handles both Success an Error states and delivers the result through a LiveData post to the view. Which in this case is GithubProfileInfoObtainmentActivity
    @UnstableDefault
    private suspend fun handleCallResult(
        user: String,
        shouldListItemsBeRemoved: Boolean = false
    ) {

        if (!flags.hasUserTriggeredANewRequest) insertTransientItemIntoTheResultsList(loading, true)

        when (val value =
            repository.provideGithubUserInformation(
                user,
                flags.pageNumber,
                numberOfItemsPerPage
            )) {

            //Success state handling
            is APICallResult.Success<*> -> {
                if (!hasLastCallBeenSuccessful()) flags.hasFirstSuccessfulCallBeenMade = true
                flags.currentProfile = flags.temporaryCurrentProfile
                flags.isThereAnOngoingCall = false
                with(value.data as GithubProfilesList) {

                    flags.isTheNumberOfItemsOfTheLastCallLessThanTwenty =
                        githubProfileInformationList.size < 20

                    if (shouldListItemsBeRemoved)
                        setupList(githubProfileInformationList)
                    else {
                        if (hasLastCallBeenSuccessful() && flags.isPaginationLoadingItemVisible) {
                            dropLast()
                        }
                        githubProfilesInfoMutableList.addAll(githubProfileInformationList)
                        if (flags.isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
                            endOfResults, true
                        )
                        githubProfilesInfoList = githubProfilesInfoMutableList
                        mainListMutableLiveData.postValue(githubProfilesInfoList)
                    }
                    flags.pageNumber++
                }
            }

            else -> handleErrorResult(value as APICallResult.Error)
        }
    }

    private fun handleErrorResult(errorValue: APICallResult.Error) {

        //Error state handling
        flags.isThereAnOngoingCall = false
        flags.hasAnyUserRequestedUpdatedData = false

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
        flags.isPaginationLoadingItemVisible = false
        flags.isRetryItemVisible = false
        flags.isEndOfResultsItemVisible = false
        githubProfilesInfoMutableList.add(Header(R.string.github_user_list_header))
        githubUserInformationList.forEach {
            populateList(it)
        }

        if (flags.isTheNumberOfItemsOfTheLastCallLessThanTwenty) insertTransientItemIntoTheResultsList(
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
                    flags.isThereAnOngoingCall = true
                    if (flags.isRetryItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(Loading())
                    flags.isPaginationLoadingItemVisible = true
                    flags.isRetryItemVisible = false
                    flags.isEndOfResultsItemVisible = false
                }

                retry -> {
                    if (flags.isPaginationLoadingItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(Retry())
                    flags.isRetryItemVisible = true
                    flags.isPaginationLoadingItemVisible = false
                    flags.isEndOfResultsItemVisible = false
                }

                else -> {
                    if (flags.isPaginationLoadingItemVisible || flags.isRetryItemVisible) dropLast()
                    githubProfilesInfoMutableList.add(EndOfResults())
                    flags.isEndOfResultsItemVisible = true
                    flags.isPaginationLoadingItemVisible = false
                    flags.isRetryItemVisible = false
                }
            }
        }

        githubProfilesInfoList = githubProfilesInfoMutableList

        if (shouldPostValue) mainListMutableLiveData.postValue(githubProfilesInfoList)
    }

    private fun dropLast() {
        dropLast(githubProfilesInfoMutableList)
    }

    private fun hasLastCallBeenSuccessful() = githubProfilesInfoList.isNotEmpty()

    //This method provides a URL to the profile a user clicks on a List item
    internal fun provideProfileUrlThroughViewModel(index: Int) =
        (githubProfilesInfoMutableList[index] as GithubProfileInformation).profileUrl
}