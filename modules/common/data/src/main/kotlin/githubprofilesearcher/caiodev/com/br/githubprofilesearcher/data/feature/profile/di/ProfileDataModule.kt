package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.di.dataModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.ProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.ProfileRemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.client.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.remote.client.ProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.repository.IProfileRemoteRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.includes
import org.koin.dsl.bind
import org.koin.dsl.lazyModule

val profileDataModule =
    lazyModule {
        includes(dataModule)
        singleOf(::ProfileDatabaseRepository) bind IProfileDatabaseRepository::class
        factoryOf(::ProfileClient) bind IProfileClient::class
        factoryOf(::ProfileRemoteRepository) bind IProfileRemoteRepository::class
    }
