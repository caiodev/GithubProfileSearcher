package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest.RestConnector.provideRetrofitService
import kotlinx.serialization.ExperimentalSerializationApi
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

//    single<IProtoDataStoreManager> {
//
//        var dataStore: DataStore<ProfilePreferences>
//
//        dataStore(fileName = profilePreferencesProtoFileName,
//            serializer = ProfileSerializer)
//
//        ProtoDataStoreManager<ProfilePreferences>()
//    }

    single<GenericGithubProfileRepository> {
        GithubProfileRepository(
            get(),
            provideRetrofitService()
        )
    }
}
