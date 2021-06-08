package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.model.diModules

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.viewModel.UserRepositoryInformationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userRepositoryViewModel = module {
    viewModel { UserRepositoryInformationViewModel() }
}
