package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.diModules

import androidx.lifecycle.SavedStateHandle
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GithubProfileRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasUserRequestedUpdatedData
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasASuccessfulCallAlreadyBeenMade
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.hasLastCallBeenUnsuccessful
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isEndOfResultsItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isPaginationLoadingItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isRetryItemVisible
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isTextInputEditTextEmpty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.isThereAnOngoingCall
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.numberOfItems
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
                set(hasASuccessfulCallAlreadyBeenMade, false)
                set(hasLastCallBeenUnsuccessful, false)
                set(hasUserRequestedUpdatedData, false)
                set(isEndOfResultsItemVisible, false)
                set(isPaginationLoadingItemVisible, false)
                set(isRetryItemVisible, false)
                set(isTextInputEditTextEmpty, true)
                set(isThereAnOngoingCall, false)
                set(numberOfItems, 0)
                set(shouldASearchBePerformed, true)
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