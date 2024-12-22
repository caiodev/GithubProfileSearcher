package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.di.profileDataModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.di.profileDomainModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.ui.viewModel.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.includes
import org.koin.dsl.lazyModule

val profileModule =
    lazyModule {
        includes(profileDataModule)
        includes(profileDomainModule)
        viewModelOf(::ProfileViewModel)
    }
