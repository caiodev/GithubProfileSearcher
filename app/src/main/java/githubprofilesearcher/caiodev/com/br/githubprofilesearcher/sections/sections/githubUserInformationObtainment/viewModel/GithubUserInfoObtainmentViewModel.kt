package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubUsersList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubUserInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubUserInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.clientSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.forbidden
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItemsPerPage
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class GithubUserInfoObtainmentViewModel(private val githubUserInformationRepository: GithubUserInformationRepository) :
    ViewModel() {

    internal val successMutableLiveData = MutableLiveData<MutableList<ViewType>>()
    internal val errorSingleLiveEvent = SingleLiveEvent<Int>()
    private val githubUsersInfoMutableList = mutableListOf<ViewType>()
    internal var hasFirstCallBeenMade = false
    private var pageNumber = 1
    internal var hasUserRequestedRefresh = false
    internal var shouldActionIconPerformSearch = false
    internal var isThereAnOngoingCall = false

    fun getGithubUsersList(
        user: String,
        shouldListItemsBeRemoved: Boolean? = null
    ) {

        shouldListItemsBeRemoved?.let {
            if (hasUserRequestedRefresh || it) pageNumber = 1
        }

        viewModelScope.launch {
            handleCallResult(user, shouldListItemsBeRemoved)
        }
    }

    private fun populateList(githubInfo: GithubUserInformation) {
        githubUsersInfoMutableList.add(
            GithubUserInformation(
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
        (githubUsersInfoMutableList[adapterPosition] as GithubUserInformation).profileUrl

    private fun setupList(
        githubUserInformationList: MutableList<GithubUserInformation>
    ) {
        if (githubUsersInfoMutableList.isNotEmpty()) githubUsersInfoMutableList.clear()
        githubUsersInfoMutableList.add(Header(R.string.github_user_list_header))
        githubUserInformationList.forEach {
            populateList(it)
        }
        successMutableLiveData.postValue(githubUsersInfoMutableList)
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
                with(value.data as GithubUsersList) {
                    shouldListItemsBeRemoved?.let {
                        if (it) setupList(githubUserInformationList)
                        else {
                            githubUsersInfoMutableList.addAll(githubUserInformationList)
                            successMutableLiveData.postValue(githubUsersInfoMutableList)
                        }
                    }
                    pageNumber++
                }
            }

            is APICallResult.Error -> {

                isThereAnOngoingCall = false
                hasUserRequestedRefresh = false

                with(errorSingleLiveEvent) {
                    when (value.error) {
                        unknownHostException, socketTimeoutException ->
                            postValue(
                                R.string.unknown_host_exception_and_socket_timeout_exception
                            )
                        sslHandshakeException -> postValue(R.string.ssl_handshake_exception)
                        clientSideError -> postValue(R.string.client_side_error)
                        serverSideError -> postValue(R.string.server_side_error)
                        forbidden -> postValue(R.string.api_query_limit_exceeded_error)
                        else -> postValue(R.string.generic_exception_and_generic_error)
                    }
                }
            }
        }
    }
}