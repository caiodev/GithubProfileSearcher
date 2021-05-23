package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.GenericProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.ProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dataStore.serializer.ProfileSerializer
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dataStore.serializer.ProfileSerializer.profileProtoFileName
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.IKeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.dataStore.manager.KeyValueStorageManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest.APIConnector.provideAPIConnector
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalSerializationApi
val githubProfileViewModel = module {

    viewModel {
        GithubProfileViewModel(
            localRepository = get(),
            remoteRepository = get()
        )
    }

    single<IKeyValueStorageManager> {
        KeyValueStorageManager(
            DataStoreFactory.create(serializer = ProfileSerializer,
                produceFile = { androidContext().dataStoreFile(profileProtoFileName) })
        )
    }

    single<GenericProfileRepository> {
        ProfileRepository(
            get(),
            provideAPIConnector()
        )
    }
}
