package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubProfileInformationRepository

class GithubProfileInfoObtainmentViewModelFactory(private val githubProfileInformationRepository: GithubProfileInformationRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GithubProfileInfoObtainmentViewModel::class.java))
            return GithubProfileInfoObtainmentViewModel(githubProfileInformationRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}