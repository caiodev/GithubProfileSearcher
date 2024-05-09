package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.di.profileUIModule
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val profileModule = module { loadKoinModules(profileUIModule) }
