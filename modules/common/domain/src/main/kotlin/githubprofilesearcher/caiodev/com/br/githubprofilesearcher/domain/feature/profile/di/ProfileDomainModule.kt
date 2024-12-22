package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.DeleteLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.FetchLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.FetchRemoteProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IDeleteLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IFetchLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IFetchRemoteProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IObtainLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.ISaveLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.IUpdateLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.ObtainLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.SaveLocalProfileUseCase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.feature.profile.usecase.UpdateLocalProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.lazyModule

val profileDomainModule =
    lazyModule {
        factoryOf(::DeleteLocalProfileUseCase) bind IDeleteLocalProfileUseCase::class
        factoryOf(::ObtainLocalProfileUseCase) bind IObtainLocalProfileUseCase::class
        factoryOf(::SaveLocalProfileUseCase) bind ISaveLocalProfileUseCase::class
        factoryOf(::UpdateLocalProfileUseCase) bind IUpdateLocalProfileUseCase::class
        factoryOf(::FetchLocalProfileUseCase) bind IFetchLocalProfileUseCase::class
        factoryOf(::FetchRemoteProfileUseCase) bind IFetchRemoteProfileUseCase::class
    }
