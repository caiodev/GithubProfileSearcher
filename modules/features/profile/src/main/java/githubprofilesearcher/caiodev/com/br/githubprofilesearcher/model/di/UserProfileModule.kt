package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository.UserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.aggregator.ProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.cell.IProfileDataObtainmentCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.cell.ProfileDataObtainmentCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository.Companion.PROFILE_PREFERENCES_INSTANCE_ID
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.ProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.calls.IProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.calls.ProfileClient
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userProfileViewModel =
    module {
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

        factory<IProfileClient> { ProfileClient(client = get()) }

        factory<IProfileOriginRepository> {
            ProfileOriginRepository(
                dispatcher = get(),
                client = get(),
                remoteFetcher = get(),
            )
        }

        factory<IProfileDataObtainmentCell> {
            ProfileDataObtainmentCell(
                keyValueRepository = get(),
                userDatabaseRepository = get(),
                profileOriginRepository = get(),
            )
        }

        factory<IProfileDataCellAggregator> {
            ProfileDataCellAggregator(
                keyValueRepository = get(),
                profileDataCell = get(),
            )
        }

        viewModel {
            ProfileViewModel(
                aggregator = get(),
            )
        }
    }
