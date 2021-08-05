package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.IProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.ProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.serializer.ProfileSerializer
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.serializer.ProfileSerializer.profileProtoFileName
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.viewModel.ProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.KeyValueStorageManager
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
            remoteRepository = get()
        )
    }

    single<IKeyValueStorageManager> {
        KeyValueStorageManager(
            keyValueStorageClient = DataStoreFactory.create(serializer = ProfileSerializer,
                produceFile = { androidContext().dataStoreFile(profileProtoFileName) })
        )
    }

    single<IProfileRepository> {
        ProfileRepository(
            remoteRepository = get(),
            apiService = get<Retrofit>().create(UserProfile::class.java)
        )
    }
}
