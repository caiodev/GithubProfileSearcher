package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dataStore.serializer.model

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.string.emptyString

data class ProfilePreferences(
    val hasUserDeletedProfileText: Boolean = false,
    val profile: String = emptyString(),
    val shouldRecyclerViewAnimationBeExecuted: Boolean = true,
    val temporaryCurrentProfile: String = emptyString(),
    val currentProfile: String = emptyString(),
    val pageNumber: Int = 1,
    val hasASuccessfulCallAlreadyBeenMade: Boolean = false,
    val hasLastCallBeenUnsuccessful: Boolean = false,
    val isThereAnOngoingCall: Boolean = false,
    val hasUserRequestedUpdatedData: Boolean = false,
    val shouldASearchBePerformed: Boolean = false,
    val isTextInputEditTextNotEmpty: Boolean = false,
    val isHeaderVisible: Boolean = false,
    val isEndOfResultsViewVisible: Boolean = false,
    val isPaginationLoadingViewVisible: Boolean = false,
    val isRetryViewVisible: Boolean = false,
    val isLocalPopulation: Boolean = false,
) {
    companion object {
        fun getDefaultInstance() = ProfilePreferences()
    }
}
