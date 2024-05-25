package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di.datasource
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.UserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueRepository.Companion.PROFILE_PREFERENCES_INSTANCE_ID
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls.ProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.ProfileOriginRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.includes
import org.koin.dsl.bind
import org.koin.dsl.lazyModule

val profileDatasourceModule =
    lazyModule {
        includes(datasource)
        factory {
            PreferenceDataStoreFactory.create {
                androidContext()
                    .preferencesDataStoreFile(PROFILE_PREFERENCES_INSTANCE_ID)
            }
        }
        factoryOf(::ProfileKeyValueRepository) bind IKeyValueRepository::class
        factoryOf(::UserDatabaseRepository) bind IUserDatabaseRepository::class
        factoryOf(::ProfileClient) bind IProfileClient::class
        factoryOf(::ProfileOriginRepository) bind IProfileOriginRepository::class
    }
