package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.dataStore

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.string.emptyString

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
