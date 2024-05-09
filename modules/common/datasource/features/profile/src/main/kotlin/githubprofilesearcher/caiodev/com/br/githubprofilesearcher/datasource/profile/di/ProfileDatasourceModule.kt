package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.UserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueRepository.Companion.PROFILE_PREFERENCES_INSTANCE_ID
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val profileDatasourceModule = module {
    single<IKeyValueRepository> {
        ProfileKeyValueRepository(
            PreferenceDataStoreFactory.create {
                androidContext()
                    .preferencesDataStoreFile(PROFILE_PREFERENCES_INSTANCE_ID)
            },
        )
    }

    factory<IUserDatabaseRepository> {
        UserDatabaseRepository(
            dispatcher = get(),
            appDatabase = get(),
        )
    }

    factory<githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls.IProfileClient> {
        githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.calls.ProfileClient(
            client = get()
        )
    }

    factory<githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.IProfileOriginRepository> {
        githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.ProfileOriginRepository(
            dispatcher = get(),
            client = get(),
            remoteFetcher = get(),
        )
    }
}
