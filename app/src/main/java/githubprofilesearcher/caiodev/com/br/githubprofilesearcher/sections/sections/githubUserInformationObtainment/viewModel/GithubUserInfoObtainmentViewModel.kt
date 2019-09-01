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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.serverSideError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.socketTimeoutException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sslHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.unknownHostException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class GithubUserInfoObtainmentViewModel : ViewModel() {

    private val successState = MutableLiveData<MutableList<ViewType>>()
    internal val successStateCopy get() = successState
    private val errorState = SingleLiveEvent<Any>()
    internal val errorStateCopy get() = errorState
    private val remoteRepository = GithubUserInformationRepository()
    private val githubUsersInfoList = mutableListOf<ViewType>()

    fun getGithubUsersList(user: String) {

        viewModelScope.launch {

            when (val value = remoteRepository.getGithubUserList(user, 30)) {

                is APICallResult.Success<*> -> {

                    with(value.data as GithubUsersList) {
                        githubUsersInfoList.clear()
                        githubUsersInfoList.add(Header(R.string.github_user_list_header))

                        githubUserInformationList.forEach {
                            populateList(it)
                        }

                        successState.postValue(githubUsersInfoList)
                    }
                }

                is APICallResult.Error<*> -> {

                    when (value.error) {
                        unknownHostException, socketTimeoutException -> errorState.postValue(R.string.unknown_host_exception_and_socket_timeout_exception)
                        sslHandshakeException -> errorState.postValue(R.string.ssl_handshake_exception)
                        clientSideError -> errorState.postValue(R.string.client_side_error)
                        serverSideError -> errorState.postValue(R.string.server_side_error)
                        else -> errorState.postValue(R.string.generic_exception_and_generic_error)
                    }
                }
            }
        }
    }

    private fun populateList(githubInfo: GithubUserInformation) {
        githubUsersInfoList.add(
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
        (githubUsersInfoList[adapterPosition] as GithubUserInformation).profileUrl
}