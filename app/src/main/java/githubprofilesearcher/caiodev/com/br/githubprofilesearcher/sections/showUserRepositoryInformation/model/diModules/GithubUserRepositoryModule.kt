package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.model.diModules

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.viewModel.UserRepositoryInformationViewModel
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UnstableDefault
val githubUserRepositoryViewModel = module {
    viewModel { UserRepositoryInformationViewModel() }
}