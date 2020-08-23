package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.diModules

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.viewModel.UserRepositoryInformationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val githubUserRepositoryViewModel = module {
    viewModel { UserRepositoryInformationViewModel() }
}
