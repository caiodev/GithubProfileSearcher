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

    fun getGithubUsersList(
        user: String,
        shouldTheListItemsBeRemoved: Boolean? = null
    ) {

        shouldTheListItemsBeRemoved?.let {
            if (hasUserRequestedRefresh || it) pageNumber = 1
        }

        viewModelScope.launch {

            when (val value =
                githubUserInformationRepository.getGithubUserList(
                    user,
                    pageNumber,
                    numberOfItemsPerPage
                )) {

                is APICallResult.Success<*> -> {
                    with(value.data as GithubUsersList) {
                        shouldTheListItemsBeRemoved?.let {
                            if (it) {
                                setupList(githubUserInformationList)
                            } else {
                                githubUsersInfoMutableList.addAll(githubUserInformationList)
                                successMutableLiveData.postValue(githubUsersInfoMutableList)
                            }
                        }
                        pageNumber++
                    }
                }

                is APICallResult.Error -> {

                    hasUserRequestedRefresh = false

                    when (value.error) {
                        unknownHostException, socketTimeoutException -> errorSingleLiveEvent.postValue(
                            R.string.unknown_host_exception_and_socket_timeout_exception
                        )
                        sslHandshakeException -> errorSingleLiveEvent.postValue(R.string.ssl_handshake_exception)
                        clientSideError -> errorSingleLiveEvent.postValue(R.string.client_side_error)
                        serverSideError -> errorSingleLiveEvent.postValue(R.string.server_side_error)
                        forbidden -> errorSingleLiveEvent.postValue(R.string.api_query_limit_exceeded_error)
                        else -> errorSingleLiveEvent.postValue(R.string.generic_exception_and_generic_error)
                    }
                }
            }
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

    fun getProfileUrlThroughViewModel(adapterPosition: Int) =
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
}