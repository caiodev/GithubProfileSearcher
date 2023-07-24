package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfileCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.ProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.IProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.ProfileDataAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueRepository.Companion.profilePreferencesInstanceID
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.ProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

@ExperimentalSerializationApi
val userProfileViewModel = module {

    factory<IKeyValueRepository> {
        ProfileKeyValueRepository(
            PreferenceDataStoreFactory.create {
                androidContext().preferencesDataStoreFile(profilePreferencesInstanceID)
            },
        )
    }

    factory<IProfileDatabaseRepository> {
        ProfileDatabaseRepository(appDatabase = get())
    }

    factory<IProfileOriginRepository> {
        ProfileOriginRepository(
            remoteRepository = get(),
            apiService = get<Retrofit>().create(UserProfileCall::class.java),
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
            networkChecking = get(),
            profileDatabaseRepository = get(),
            profileOriginRepository = get(),
        )
    }
}
