package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.di.profileMidfieldModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewModel.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.includes
import org.koin.dsl.lazyModule

val profileUIModule =
    lazyModule {
        includes(profileMidfieldModule)
        viewModelOf(::ProfileViewModel)
    }
