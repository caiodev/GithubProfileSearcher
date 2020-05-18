package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.diModules

import androidx.lifecycle.SavedStateHandle
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasAnyUserRequestedUpdatedData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasFirstSuccessfulCallBeenMade
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserTriggeredANewRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isEndOfResultsItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isPaginationLoadingItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isRetryItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnOngoingCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnyProfileToBeSearched
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.shouldASearchBePerformed
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.factory.Retrofit.provideRetrofitService
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@UnstableDefault
val githubProfileViewModel = module {

    viewModel {
        GithubProfileViewModel(
            savedStateHandle = get(), repository = get()
        )
    }

    single {
        SavedStateHandle(
            hashMapOf<String, Any>().apply {
                set(hasFirstSuccessfulCallBeenMade, false)
                set(isThereAnOngoingCall, false)
                set(hasUserTriggeredANewRequest, false)
                set(hasAnyUserRequestedUpdatedData, false)
                set(shouldASearchBePerformed, true)
                set(isThereAnyProfileToBeSearched, false)
                set(isEndOfResultsItemVisible, false)
                set(isPaginationLoadingItemVisible, false)
                set(isRetryItemVisible, false)
            }
        )
    }

    single<GenericGithubProfileRepository> {
        GithubProfileRepository(
            RemoteRepository(),
            provideRetrofitService()
        )
    }
}