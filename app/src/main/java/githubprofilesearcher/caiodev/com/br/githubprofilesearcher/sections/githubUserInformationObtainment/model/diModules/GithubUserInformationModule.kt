package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.diModules

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.provideRetrofitService
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UnstableDefault
val githubProfileViewModel = module {

    viewModel { GithubProfileViewModel(repository = get()) }

    single<GenericGithubProfileRepository> {
        GithubProfileRepository(
            RemoteRepository(),
            provideRetrofitService()
        )
    }
}