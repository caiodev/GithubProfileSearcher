package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.ProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.IProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.ProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository.Companion.PROFILE_PREFERENCES_INSTANCE_ID
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.ProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.calls.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.calls.ProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalSerializationApi
val userProfileViewModel =
    module {

        factory<IKeyValueRepository> {
            ProfileKeyValueRepository(
                PreferenceDataStoreFactory.create {
                    androidContext().preferencesDataStoreFile(PROFILE_PREFERENCES_INSTANCE_ID)
                },
            )
        }

        factory<IProfileDatabaseRepository> {
            ProfileDatabaseRepository(appDatabase = get())
        }

        factory<IProfileClient> { ProfileClient(client = get()) }

        factory<IProfileOriginRepository> {
            ProfileOriginRepository(
                remoteFetcher = get(),
                client = get(),
            )
        }

        factory<IProfileDataAggregator> {
            ProfileDataAggregator(
                keyValueRepository = get(),
                profileDatabaseRepository = get(),
                profileOriginRepository = get(),
            )
        }

        viewModel {
            ProfileViewModel(
                profileDataAggregator = get(),
            )
        }
    }
