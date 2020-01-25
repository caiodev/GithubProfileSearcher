package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.koinModule

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.GithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.Repository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.viewModel.GithubProfileInfoObtainmentViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.provideRetrofitService
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UnstableDefault
val githubProfileInfoObtainmentViewModel = module {
    viewModel { GithubProfileInfoObtainmentViewModel(repository = get()) }
    single { GithubProfileInformationRepository(provideRetrofitService()) as Repository }
}