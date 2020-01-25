package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showUserRepositoryInformation.model.koinModule

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.showUserRepositoryInformation.model.repository.GithubRepositoryInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.provideRetrofitService
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UnstableDefault
val githubUserRepositoryViewModel = module {
    viewModel { GithubProfileInfoObtainmentViewModel(repository = get()) }
    single { GithubRepositoryInformationRepository(provideRetrofitService()) }
}