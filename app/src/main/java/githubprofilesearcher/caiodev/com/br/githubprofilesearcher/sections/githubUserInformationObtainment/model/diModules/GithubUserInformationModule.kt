package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.diModules

import android.content.Context
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.baseUrl
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.sharedPreferences
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

    single<GenericLocalRepository> {
        LocalRepository(
            androidContext().getSharedPreferences(
                sharedPreferences,
                Context.MODE_PRIVATE
            ), get()
        )
    }

    single<GenericGithubProfileRepository> {
        GithubProfileRepository(
            RemoteRepository(),
            provideRetrofitService(baseUrl)
        )
    }
}
