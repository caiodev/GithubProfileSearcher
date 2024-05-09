package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.di.profileMidfieldModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewModel.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val profileUIModule =
    module {
        loadKoinModules(profileMidfieldModule)
        viewModel {
            ProfileViewModel(
                dispatcher = get(),
                aggregator = get(),
            )
        }
    }
