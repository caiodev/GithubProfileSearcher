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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.toSingleEvent
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
    internal val errorSingleImmutableLiveDataEvent = errorSingleMutableLiveDataEvent.toSingleEvent()

    private val githubProfilesInfoMutableList = mutableListOf<ViewType>()
    private var githubProfilesInfoList = listOf<ViewType>()

    private val statePair = Pair(0, 0)

    internal var haveUsersHadAnyTroubleDuringTheirFirstCall = false
    internal var hasFirstCallBeenMade = false
    private var pageNumber = 1
    internal var hasUserRequestedUpdatedData = false
    internal var shouldASearchBePerformed = false
    internal var isThereAnOngoingCall = false
    private var currentProfile = ""

    fun getGithubProfileList(
        profile: String? = null,
        shouldListItemsBeRemoved: Boolean? = null
    ) {

        profile?.let {
            currentProfile = it
        }

        shouldListItemsBeRemoved?.let {
            if (hasUserRequestedUpdatedData || it) pageNumber = 1
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
                isThereAnOngoingCall = false
                with(value.data as GithubProfilesList) {
                    shouldListItemsBeRemoved?.let {
                        if (it) setupList(githubProfileInformationList)
                        else {
                            githubProfilesInfoMutableList.addAll(githubProfileInformationList)
                            successMutableLiveData.postValue(githubProfilesInfoMutableList)
                        }
                    }
                    pageNumber++
                }
            }

            is APICallResult.Error -> {

                isThereAnOngoingCall = false
                hasUserRequestedUpdatedData = false

                with(errorSingleMutableLiveDataEvent) {
                    when (value.error) {
                        unknownHostException, socketTimeoutException, connectException ->
                            postValue(
                                errorPairProvider(
                                    unknownHostException,
                                    R.string.unknown_host_exception_and_socket_timeout_exception
                                )
                            )
                        sslHandshakeException -> postValue(
                            errorPairProvider(
                                sslHandshakeException,
                                R.string.ssl_handshake_exception
                            )
                        )
                        clientSideError -> postValue(
                            errorPairProvider(
                                clientSideError,
                                R.string.client_side_error
                            )
                        )
                        serverSideError -> postValue(
                            errorPairProvider(
                                serverSideError,
                                R.string.server_side_error
                            )
                        )
                        forbidden -> postValue(
                            errorPairProvider(
                                forbidden,
                                R.string.api_query_limit_exceeded_error
                            )
                        )
                        else -> postValue(
                            errorPairProvider(
                                genericError,
                                R.string.generic_exception_and_generic_error
                            )
                        )
                    }
                }
            }
        }
    }

    private fun errorPairProvider(errorState: Int, errorString: Int): Pair<Int, Int> {
        return statePair.copy(errorState, errorString)
    }
}