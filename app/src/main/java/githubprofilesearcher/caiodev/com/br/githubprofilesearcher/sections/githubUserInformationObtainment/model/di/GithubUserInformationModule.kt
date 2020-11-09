package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.di

import androidx.datastore.createDataStore
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager.IProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager.ProtoDataStoreManager
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.manager.ProtoDataStoreManager.Companion.protoFileName
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.serializer.UserPreferencesSerializer
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.baseUrl
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.provideRetrofitService
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

    single<IProtoDataStoreManager> {
        ProtoDataStoreManager(
            androidContext().createDataStore(
                fileName = protoFileName,
                serializer = UserPreferencesSerializer
            )
        )
    }

    single<GenericLocalRepository> {
        LocalRepository(
            get(),
            get()
        )
    }

    single<GenericGithubProfileRepository> {
        GithubProfileRepository(
            RemoteRepository(),
            provideRetrofitService(baseUrl)
        )
    }
}
