package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.diModules

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.viewModel.RepositoryInformationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userRepositoryViewModel = module {
    viewModel { RepositoryInformationViewModel() }
}
