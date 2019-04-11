package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.UserRepositoryInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.repository.LocalUserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.repoInformationObtainment.model.repository.RemoteUserRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.launch

class RepoInfoObtainmentViewModel : ViewModel() {

    private val state = MutableLiveData<Any>()
    private val localUserRepository = LocalUserRepository()
    private val remoteUserRepository = RemoteUserRepository()
    private var isUserInfoLoaded = false

    fun getGithubUserInformation(user: String) {

        viewModelScope.launch {

            when (val value = remoteUserRepository.getRepositoryInformation(user)) {

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

    internal fun insertValueIntoSharedPreferences(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String
    ) {
        localUserRepository.insertValueIntoSharedPreferences(sharedPreferences, key, value)
    }

    internal fun getValueFromSharedPreferences(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String? = null
    ) = localUserRepository.getValueFromSharedPreferences(sharedPreferences, key, value)

    internal fun isUserInfoLoaded() = isUserInfoLoaded

    internal fun setUserInfoLoadingStatus(status: Boolean) {
        isUserInfoLoaded = status
    }

    companion object {
        const val onRepositoryObtainmentFailure = 1
    }
}