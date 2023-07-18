package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfileCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager.IKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValueManager.KeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore.ProfileSerializer
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore.ProfileSerializer.profileProtoFileName
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.ProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

@ExperimentalSerializationApi
val userProfileViewModel = module {

    viewModel {
        ProfileViewModel(
            networkChecking = get(),
            localRepository = get(),
            remoteRepository = get(),
        )
    }

    factory<IKeyValueStorageManager> {
        KeyValueStorageManager(
            keyValueStorageClient = DataStoreFactory.create(
                serializer = ProfileSerializer,
                produceFile = { androidContext().dataStoreFile(profileProtoFileName) },
            ),
        )
    }

    factory<IProfileRepository> {
        ProfileRepository(
            remoteRepository = get(),
            apiService = get<Retrofit>().create(UserProfileCall::class.java),
        )
    }
}