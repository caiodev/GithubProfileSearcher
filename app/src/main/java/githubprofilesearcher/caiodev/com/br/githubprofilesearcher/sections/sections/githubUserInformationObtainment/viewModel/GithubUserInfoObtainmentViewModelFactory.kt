package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubUserInformationRepository

class GithubUserInfoObtainmentViewModelFactory(private val githubUserInformationRepository: GithubUserInformationRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GithubUserInfoObtainmentViewModel::class.java))
            return GithubUserInfoObtainmentViewModel(githubUserInformationRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}