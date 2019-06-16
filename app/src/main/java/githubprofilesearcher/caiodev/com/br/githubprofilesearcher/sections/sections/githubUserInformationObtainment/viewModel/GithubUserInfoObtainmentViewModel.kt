package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubUsersList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubUserInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.GithubUserInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Header
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.SingleLiveEvent
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.getValueFromSharedPreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.insertValueIntoSharedPreferences
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.viewTypes.ViewType
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class GithubUserInfoObtainmentViewModel : ViewModel() {

    private val successState = MutableLiveData<MutableList<ViewType>>()
    internal val successStateCopy get() = successState
    private val errorState = SingleLiveEvent<Any>()
    internal val errorStateCopy get() = errorState
    private val localRepository = LocalRepository()
    private val remoteRepository = GithubUserInformationRepository()
    private val githubUsersInfoList = mutableListOf<ViewType>()

    fun getGithubUsersList(user: String) {

        viewModelScope.launch {

            when (val value = remoteRepository.getGithubUserList(user, 30)) {

                is APICallResult.Success<*> -> {

                    githubUsersInfoList.clear()

                    githubUsersInfoList.add(Header(R.string.github_user_list_header))

                    with(value.data as GithubUsersList) {

                        githubUserInformationList.forEach {
                            populateList(it)
                        }

                        successState.postValue(githubUsersInfoList)
                    }
                }

                is APICallResult.InternalError<*> -> errorState.postValue(value.error as String)

                else -> errorState.postValue(onRepositoryObtainmentFailure)
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

    fun getValueThroughViewModel(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String
    ) {
        insertValueIntoSharedPreferences(localRepository, sharedPreferences, key, value)
    }

    fun insertValueThroughViewModel(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String
    ) {
        getValueFromSharedPreferences(localRepository, sharedPreferences, key, value)
    }

    fun getProfileUrlThroughViewModel(adapterPosition: Int) =
        (githubUsersInfoList[adapterPosition] as GithubUserInformation).profileUrl

    companion object {
        const val onRepositoryObtainmentFailure = 1
    }
}