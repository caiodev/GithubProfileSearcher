package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.UserRepositoryInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.repository.UserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class RepoInfoObtainmentViewModel : ViewModel() {

    private val state = MutableLiveData<Any>()
    private val repository = UserRepository()
    private var isUserInfoLoaded = false

    fun getGithubUserInformation(user: String) {

        viewModelScope.launch {

            val value = repository.getRepositoryInformation(user)

            when (value) {

                is APICallResult.Success<*> -> with(value.data as UserRepositoryInformation) {
                    state.postValue(this)
                }

                is APICallResult.InternalError<*> -> with(value.error as String) {
                    state.postValue(this)
                }

                else -> state.postValue(onRepositoryObtainmentFailure)
            }
        }
    }

    internal fun getLiveData() = state

    internal fun isUserInfoLoaded() = isUserInfoLoaded

    internal fun setUserInfoLoadingStatus(status: Boolean) {
        isUserInfoLoaded = status
    }

    companion object {
        const val onRepositoryObtainmentFailure = 1
    }
}