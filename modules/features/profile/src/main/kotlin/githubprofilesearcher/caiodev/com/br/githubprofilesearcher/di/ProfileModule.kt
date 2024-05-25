package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.di.profileUIModule
import org.koin.core.module.includes
import org.koin.dsl.lazyModule

val profileModule = lazyModule { includes(profileUIModule) }
